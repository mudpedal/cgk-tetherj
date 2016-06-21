package com.cegeka.tetherj.api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.cegeka.tetherj.EthEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cegeka.tetherj.EthCall;
import com.cegeka.tetherj.EthRpcClient;
import com.cegeka.tetherj.EthSignedTransaction;
import com.cegeka.tetherj.EthTransaction;
import com.cegeka.tetherj.EthWallet;
import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.pojo.Block;
import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.FilterLogObject;
import com.cegeka.tetherj.pojo.FilterLogRequest;
import com.cegeka.tetherj.pojo.Transaction;
import com.cegeka.tetherj.pojo.TransactionReceipt;
import com.googlecode.jsonrpc4j.HttpException;
import com.googlecode.jsonrpc4j.JsonRpcClientException;

/**
 * Implementation for an Ethereum service api.
 *
 * @author Andrei Grigoriu
 */
public class EthereumService {

    public static final int DEFAULT_EXECUTOR_THREADS = 2;
    public static final int ASYNC_FILTER_BLOCK_SPLIT = 1000;

    /**
     * A generic wrapper for a rpc call.
     *
     * @param <T> return type of rpc call
     * @author Andrei Grigoriu
     */
    public interface RpcAction<T> {

        /**
         * Execute rpc call here.
         *
         * @return rpc response
         */
        public T call();
    }

    /**
     * Interval between receipt check polls.
     */
    public static final int RECEIPT_CHECK_INTERVAL_MILLIS = 1000;

    /**
     * Max receipt checks to do.
     */
    public static final int RECEIPT_MAX_CHECKS = 60 * 1000 * 10 / RECEIPT_CHECK_INTERVAL_MILLIS;

    private final EthRpcClient rpc;
    private final ScheduledExecutorService executor;

    private static final Logger logger = LogManager.getLogger(EthereumService.class);

    /**
     * Constructor. Creates executor automatically
     */
    public EthereumService() {
        this(EthereumService.DEFAULT_EXECUTOR_THREADS, EthRpcClient.DEFAULT_HOSTNAME,
            EthRpcClient.DEFAULT_PORT);
    }

    /**
     * Creates executor with custom number of threads.
     *
     * @param executorThreads to spawn, 0 to disable async support
     */
    public EthereumService(int executorThreads) {
        this(executorThreads, EthRpcClient.DEFAULT_HOSTNAME, EthRpcClient.DEFAULT_PORT);
    }

    /**
     * Creates custom number of threads and custom ethereum client connection info.
     *
     * @param executorThreads to spawn, 0 to disable async support
     * @param rpcHostname     of the ethereum client
     * @param port            of the ethereum client
     */
    public EthereumService(int executorThreads, String rpcHostname, int port) {

        if (executorThreads > 0) {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(executorThreads,
                new ThreadFactory() {

                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable);

                        thread.setUncaughtExceptionHandler(
                            new Thread.UncaughtExceptionHandler() {
                                @Override
                                public void uncaughtException(Thread thread, Throwable ex) {
                                    handleUnknownThrowables(ex);
                                }
                            }
                        );

                        return thread;
                    }
                });

            this.executor = executor;
            logger.info("Created ethereum service with async support on " + executorThreads
                + " threads!");
        } else {
            /* when 0 threads specified, service is always blocking */
            this.executor = null;
            logger.info("Created ethereum service with no async support!");
        }

        rpc = new EthRpcClient(rpcHostname, port);
        logger.info("Created ethereum service");
    }

    /**
     * Constructor.
     *
     * @param executor to use for async and future calls, also for polling, null to disable async
     *                 support
     */
    public EthereumService(ScheduledExecutorService executor) {
        this(executor, EthRpcClient.DEFAULT_HOSTNAME, EthRpcClient.DEFAULT_PORT);
    }

    /**
     * Constructor.
     *
     * @param executor    to use for async and future calls, also for polling, null to disable
     *                    async support
     * @param rpcHostname ethereum client hostname
     */
    public EthereumService(ScheduledExecutorService executor, String rpcHostname) {
        this(executor, rpcHostname, EthRpcClient.DEFAULT_PORT);
    }

    /**
     * Constructor.
     *
     * @param executor    to use for async and future calls, also for polling
     * @param rpcHostname ethereum client hostname
     * @param port        ethereum client port
     */
    public EthereumService(ScheduledExecutorService executor, String rpcHostname, int port) {
        this.executor = executor;
        rpc = new EthRpcClient(rpcHostname, port);

        if (this.executor != null) {
            logger.info("Created ethereum service with async support on custom executor!");
        } else {
            logger.info("Created ethereum service with no async support!");
        }
    }

    /**
     * Call this when you don't know what to do with ex.
     *
     * @param ex the exception thrown somewhere
     */
    private void handleUnknownThrowables(Throwable ex) {
        if (ex != null) {

            StringWriter exceptionStringWriter = new StringWriter();
            PrintWriter exceptionPrintWriter = new PrintWriter(exceptionStringWriter);
            ex.printStackTrace(exceptionPrintWriter);
            String exceptionStack = exceptionStringWriter.toString();

            String message = "Tetherj uncaught exception: " + ex.toString() + " " + ex.getMessage()
                + " at \n" + exceptionStack;

            if (ex.getCause() != null) {
                StringWriter throwableStringWriter = new StringWriter();
                PrintWriter throwablePrintWriter = new PrintWriter(throwableStringWriter);
                Throwable throwable = ex.getCause();
                throwable.printStackTrace(throwablePrintWriter);
                String throwableStack = throwableStringWriter.toString();

                message += "\n CAUSE: " + throwable.toString() + " " + throwable.getMessage()
                    + " at \n" + throwableStack;
            }

            logger.error(message);
        }
    }

    /**
     * Generate wallet with a random key pair.
     *
     * @param passphrase to crypt wallet with
     * @return the wallet
     */
    public EthWallet createWallet(String passphrase) {
        return EthWallet.createWallet(passphrase);
    }

    /**
     * Get internal rpc client used to communicate with the ethereum client.
     *
     * @return rpc client
     */
    public EthRpcClient getRpcClient() {
        return rpc;
    }

    /**
     * Blocking execute of rpc action. Wraps errors into a tetherj response.
     *
     * @param rpcAction to execute
     * @return response
     */
    private <T> TetherjResponse<T> performBlockingRpcAction(RpcAction<T> rpcAction) {
        ErrorType err = null;
        Exception ex = null;
        T rpcResponse = null;

        try {
            rpcResponse = rpcAction.call();
        } catch (JsonRpcClientException rpcException) {
            err = ErrorType.BLOCKCHAIN_CLIENT_OPERATION_ERROR;
            ex = rpcException;
        } catch (UndeclaredThrowableException | HttpException httpException) {
            err = ErrorType.BLOCKCHAIN_CLIENT_BAD_CONNECTION;
            ex = httpException;
        } catch (Exception generalException) {
            err = ErrorType.UNKNOWN_ERROR;
            ex = generalException;
        }

        return new TetherjResponse<T>(err, ex, rpcResponse);
    }

    /**
     * Async execute of rpc action. Runs callable handle when done.
     *
     * @param rpcAction to execute
     * @param callable  to execute after rpcAction operation ends
     */
    private <T> void performAsyncRpcAction(RpcAction<T> rpcAction, TetherjHandle<T> callable) {
        boolean asyncExecution = executorAsync(() -> {
            try {
                callable.call(performBlockingRpcAction(rpcAction));
            } catch (Throwable throwable) {
                handleUnknownThrowables(throwable);
            }
        });

        if (!asyncExecution) {
            /* no async available, execute it blocking */
            callable.call(performBlockingRpcAction(rpcAction));
        }
    }

    private boolean executorAsync(Runnable runnable) {
        if (executor != null && !executor.isShutdown()) {
            synchronized (executor) {
                try {
                    executor.submit(runnable);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Async execute of rpc action. Returns a future to get when operation ends.
     *
     * @param rpcAction to execute
     */
    private <T> Future<TetherjResponse<T>> performFutureRpcAction(RpcAction<T> rpcAction) {
        if (executor != null && !executor.isShutdown()) {
            synchronized (executor) {
                try {
                    return executor.submit(new Callable<TetherjResponse<T>>() {

                        @Override
                        public TetherjResponse<T> call() throws Exception {
                            return performBlockingRpcAction(rpcAction);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        } else {
            return CompletableFuture.completedFuture(performBlockingRpcAction(rpcAction));
        }
    }

    /**
     * Async get accounts registered in the ethereum client.
     *
     * @param callable to call after the accounts are fetched
     */
    public void getAccounts(TetherjHandle<String[]> callable) {
        performAsyncRpcAction(new RpcAction<String[]>() {

            @Override
            public String[] call() {
                return rpc.getAccounts();
            }

        }, callable);
    }

    /**
     * Blocking get accounts registered in the ethereum client.
     *
     * @return rpc accounts response
     */
    public TetherjResponse<String[]> getAccounts() {
        return performBlockingRpcAction(new RpcAction<String[]>() {

            @Override
            public String[] call() {
                return rpc.getAccounts();
            }
        });
    }

    /**
     * Future get accounts registered in the ethereum client.
     *
     * @return Future for the accounts response.
     */
    public Future<TetherjResponse<String[]>> getAccountsFuture() {
        return performFutureRpcAction(new RpcAction<String[]>() {

            @Override
            public String[] call() {
                return rpc.getAccounts();
            }
        });
    }

    /**
     * Async get latest block number from ethereum client.
     *
     * @param callable to call after the block number is fetched
     */
    public void getLatestBlockNumber(TetherjHandle<BigInteger> callable) {
        performAsyncRpcAction(() -> rpc.getLatestBlockNumber(), callable);
    }

    /**
     * Blocking get latest block number from ethereum client.
     *
     * @return latest block number response
     */
    public TetherjResponse<BigInteger> getLatestBlockNumber() {
        return performBlockingRpcAction(() -> rpc.getLatestBlockNumber());
    }

    /**
     * Future get latest block number from ethereum client.
     *
     * @return Future for the accounts response.
     */
    public Future<TetherjResponse<BigInteger>> getLatestBlockNumberFuture() {
        return performFutureRpcAction(() -> rpc.getLatestBlockNumber());
    }

    /**
     * Async get balance of an account.
     *
     * @param address  to get balance of.
     * @param callable to execute when balance is fetched
     */
    public void getBalance(final String address, TetherjHandle<BigInteger> callable) {
        performAsyncRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getBalance(address);
            }

        }, callable);
    }

    /**
     * Blocking get balance of an account.
     *
     * @param address to get balance of
     * @return response with balance
     */
    public TetherjResponse<BigInteger> getBalance(final String address) {
        return performBlockingRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getBalance(address);
            }
        });
    }

    /**
     * Future get balance of an account
     *
     * @param address to get balance of
     * @return future to get balance response.
     */
    public Future<TetherjResponse<BigInteger>> getBalanceFuture(final String address) {
        return performFutureRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getBalance(address);
            }
        });
    }

    /**
     * Async get account nonce of address (does not currently count pending executions)
     *
     * @param address  to get account nonce of.
     * @param callable to execute with nonce response
     */
    public void getAccountNonce(final String address, TetherjHandle<BigInteger> callable) {
        performAsyncRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getAccountNonce(address);
            }

        }, callable);
    }

    /**
     * Blocking get account nonce of address (does not currently count pending executions)
     *
     * @param address to get account nonce of.
     * @return nonce response
     */
    public TetherjResponse<BigInteger> getAccountNonce(final String address) {
        return performBlockingRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getAccountNonce(address);
            }
        });
    }

    /**
     * Future get account nonce of address (does not currently count pending executions).
     *
     * @param address to get nonce for
     * @return future to get nonce response
     */
    public Future<TetherjResponse<BigInteger>> getAccountNonceFuture(final String address) {
        return performFutureRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getAccountNonce(address);
            }
        });
    }

    /**
     * Async get account nonce of address (does not currently count pending executions)
     *
     * @param address  to get account nonce of.
     * @param callable to execute with nonce response
     */
    public void getAccountNonceWithPending(final String address,
        TetherjHandle<BigInteger> callable) {
        performAsyncRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getAccountNonceWithPending(address);
            }

        }, callable);
    }

    /**
     * Blocking get account nonce of address (does not currently count pending executions)
     *
     * @param address to get account nonce of.
     * @return nonce response
     */
    public TetherjResponse<BigInteger> getAccountNonceWithPending(final String address) {
        return performBlockingRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getAccountNonceWithPending(address);
            }
        });
    }

    /**
     * Future get account nonce of address (does not currently count pending executions).
     *
     * @param address to get nonce for
     * @return future to get nonce response
     */
    public Future<TetherjResponse<BigInteger>> getAccountNonceWithPendingFuture(
        final String address) {
        return performFutureRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return rpc.getAccountNonceWithPending(address);
            }
        });
    }

    /**
     * Future send transaction.
     *
     * @param from        wallet to sign transaction with
     * @param transaction to send
     * @param nonce       to sign transaction with
     * @return future to get response for transaction hash.
     * @throws WalletLockedException if wallet is locked
     */
    public Future<TetherjResponse<String>> sendTransactionFuture(EthWallet from,
        EthTransaction transaction, BigInteger nonce) throws WalletLockedException {
        EthSignedTransaction txSigned = transaction.signWithWallet(from, nonce);
        byte[] rawEncoded = txSigned.getSignedEncodedData();

        return performFutureRpcAction(new RpcAction<String>() {

            @Override
            public String call() {
                logger.debug("Sending transaction {from:" + from.getAddress() + ", nonce: " + nonce
                    + " " + transaction.toString());
                return rpc.sendRawTransaction(rawEncoded);
            }
        });
    }

    /**
     * Blocking send transaction. Generates nonce automatically (by rpc)
     *
     * @param from        wallet to sign transaction with
     * @param transaction to send
     * @return response for transaction hash
     * @throws WalletLockedException if wallet is locked
     */
    public TetherjResponse<String> sendTransaction(EthWallet from, EthTransaction transaction)
        throws WalletLockedException {
        TetherjResponse<BigInteger> nonceResponse = getAccountNonceWithPending(from.getAddress());

        if (nonceResponse.getErrorType() == null) {
            return sendTransaction(from, transaction, nonceResponse.getValue());
        }

        return new TetherjResponse<String>(nonceResponse);
    }

    /**
     * Blocking send transaction.
     *
     * @param from        wallet to sign transaction with
     * @param transaction to send
     * @param nonce       to sign transaction with
     * @return response for transaction hash
     * @throws WalletLockedException if wallet is locked
     */
    public TetherjResponse<String> sendTransaction(EthWallet from, EthTransaction transaction,
        BigInteger nonce) throws WalletLockedException {
        EthSignedTransaction txSigned = transaction.signWithWallet(from, nonce);
        byte[] rawEncoded = txSigned.getSignedEncodedData();

        return performBlockingRpcAction(new RpcAction<String>() {

            @Override
            public String call() {
                logger.debug("Sending transaction {from:" + from.getAddress() + ", nonce: " + nonce
                    + " " + transaction.toString());
                return rpc.sendRawTransaction(rawEncoded);
            }
        });
    }

    /**
     * Async send transaction. Nonce gets generated automatically via rpc.
     *
     * @param from        wallet to sign transaction with
     * @param transaction to send
     * @param callable    to execute when transaction was submitted.
     */
    public void sendTransaction(EthWallet from, EthTransaction transaction,
        TetherjHandle<String> callable) {
        String address = from.getAddress();
        getAccountNonceWithPending(address, new TetherjHandle<BigInteger>() {

            @Override
            public void call(TetherjResponse<BigInteger> response) {
                if (response.getErrorType() == null) {
                    sendTransaction(from, transaction, response.getValue(), callable);
                } else {
                    callable.call(new TetherjResponse<>(response));
                }
            }
        });
    }

    /**
     * Async send transaction.
     *
     * @param from        wallet to sign transaction with
     * @param transaction to send
     * @param nonce       to sign transaction with
     * @param callable    to execute when transaction was submitted.
     */
    public void sendTransaction(EthWallet from, EthTransaction transaction, BigInteger nonce,
        TetherjHandle<String> callable) {
        try {
            EthSignedTransaction txSigned = transaction.signWithWallet(from, nonce);
            byte[] rawEncoded = txSigned.getSignedEncodedData();

            performAsyncRpcAction(new RpcAction<String>() {

                @Override
                public String call() {
                    return rpc.sendRawTransaction(rawEncoded);
                }

            }, callable);

        } catch (WalletLockedException ex) {
            callable.call(new TetherjResponse<String>(ErrorType.BAD_STATE, ex));
        }
    }

    /**
     * Blocking sign transaction.
     *
     * @param transaction signed transaction to send
     * @return response for signed transaction
     * @throws WalletLockedException if wallet is locked
     */
    public TetherjResponse<EthSignedTransaction> signTransaction(EthTransaction transaction,
        EthWallet wallet) throws WalletLockedException {
        String from = wallet.getAddress();
        TetherjResponse<BigInteger> nonceResponse = getAccountNonceWithPending(from);

        if (nonceResponse.getErrorType() != null) {
            return new TetherjResponse<EthSignedTransaction>(nonceResponse);
        }

        EthSignedTransaction txSigned = transaction.signWithWallet(wallet,
            nonceResponse.getValue());
        return new TetherjResponse<EthSignedTransaction>(null, null, txSigned);
    }

    /**
     * Sign transaction.
     *
     * @param transaction signed transaction to send
     * @return the signed transaction
     * @throws WalletLockedException if wallet is locked
     */
    public EthSignedTransaction signTransaction(EthTransaction transaction, EthWallet wallet,
        BigInteger nonce) throws WalletLockedException {
        return transaction.signWithWallet(wallet, nonce);
    }

    /**
     * Blocking send signed transaction.
     *
     * @param transaction signed transaction to send
     * @return response for transaction hash
     */
    public TetherjResponse<String> sendSignedTransaction(EthSignedTransaction transaction) {

        return performBlockingRpcAction(new RpcAction<String>() {

            @Override
            public String call() {
                logger.debug("Sending transaction {from:" + transaction.getFrom() + " "
                    + transaction.toString());
                return rpc.sendRawTransaction(transaction.getSignedEncodedData());
            }
        });
    }

    /**
     * Async send signed transaction.
     *
     * @param transaction signed transaction to send
     * @param callable    to execute when transaction was submitted.
     */
    public void sendSignedTransaction(EthSignedTransaction transaction,
        TetherjHandle<String> callable) {

        performAsyncRpcAction(new RpcAction<String>() {

            @Override
            public String call() {
                return rpc.sendRawTransaction(transaction.getSignedEncodedData());
            }

        }, callable);
    }

    /**
     * Future send signed transaction.
     *
     * @param transaction signed transaction to send
     */
    public Future<TetherjResponse<String>> sendSignedTransactionFuture(
        EthSignedTransaction transaction) {

        return performFutureRpcAction(new RpcAction<String>() {

            @Override
            public String call() {
                return rpc.sendRawTransaction(transaction.getSignedEncodedData());
            }

        });
    }

    /**
     * Async listen for tx receipt. Will call when transaction is mined or was already mined.
     *
     * @param txHash         transaction hash to listen for
     * @param secondsTimeout seconds until you want give up listening
     * @param callable       to execute when transaction is mined
     */
    public void listenForTxReceipt(final String txHash, int secondsTimeout,
        final TetherjHandle<TransactionReceipt> callable) {
        int checks = secondsTimeout * 1000 / RECEIPT_CHECK_INTERVAL_MILLIS;
        listenForTxReceipt(txHash, RECEIPT_CHECK_INTERVAL_MILLIS, checks, callable);
    }

    /**
     * Async listen for tx receipt. Will call when transaction is mined or was already mined.
     *
     * @param txHash   transaction hash to listen for
     * @param callable to execute when transaction is mined
     */
    public void listenForTxReceipt(final String txHash,
        final TetherjHandle<TransactionReceipt> callable) {
        listenForTxReceipt(txHash, RECEIPT_CHECK_INTERVAL_MILLIS, RECEIPT_MAX_CHECKS, callable);
    }

    private void listenForTxReceipt(final String txHash, final int checkIntervalMillis,
        final int checks, final TetherjHandle<TransactionReceipt> callable) {

        performAsyncRpcAction(new RpcAction<TransactionReceipt>() {

            @Override
            public TransactionReceipt call() {
                TransactionReceipt receipt = rpc.getTransactionReceipt(txHash);
                return receipt;
            }
        }, new TetherjHandle<TransactionReceipt>() {

            @Override
            public void call(TetherjResponse<TransactionReceipt> response) {
                if (response.getErrorType() != null) {
                    callable.call(response);
                } else {
                    TransactionReceipt receipt = response.getValue();

                    if (receipt != null && receipt.getBlockNumber() != null) {
                        callable.call(response);
                    } else if (checks <= 0) {
                        callable.call(new TetherjResponse<TransactionReceipt>(
                            ErrorType.OPERATION_TIMEOUT, new TxReceiptTimeoutException()));
                    } else {
                        if (executor != null && !executor.isShutdown()) {
                            synchronized (executor) {
                                executor.schedule(new Runnable() {

                                    @Override
                                    public void run() {
                                        listenForTxReceipt(txHash, checkIntervalMillis, checks - 1,
                                            callable);
                                    }
                                }, checkIntervalMillis, TimeUnit.MILLISECONDS);
                            }
                        } else {
                            callable.call(new TetherjResponse<TransactionReceipt>(
                                ErrorType.OPERATION_TIMEOUT, new TxReceiptTimeoutException()));
                        }
                    }
                }
            }
        });
    }

    /**
     * Async calls and fetches the output of an ethereum function.
     *
     * @param call     to execute on the ethereum chain
     * @param callable to execute with output data.
     */
    public void makeCall(final EthCall call, TetherjHandle<Object[]> callable) {
        performAsyncRpcAction(new RpcAction<Object[]>() {

            @Override
            public Object[] call() {
                return call.decodeOutput(rpc.callMethod(call));
            }

        }, callable);
    }

    /**
     * Blocking calls and fetches the output of an ethereum function.
     *
     * @param call to execute on the ethereum chain
     * @return output response
     */
    public TetherjResponse<Object[]> makeCall(final EthCall call) {
        return performBlockingRpcAction(new RpcAction<Object[]>() {

            @Override
            public Object[] call() {
                return call.decodeOutput(rpc.callMethod(call));
            }
        });
    }

    /**
     * Future calls and fetches the output of an ethereum function.
     *
     * @param call to execute on the ethereum chain
     * @return future to get output response
     */
    public Future<TetherjResponse<Object[]>> makeCallFuture(final EthCall call) {
        return performFutureRpcAction(new RpcAction<Object[]>() {

            @Override
            public Object[] call() {
                return call.decodeOutput(rpc.callMethod(call));
            }
        });
    }

    /**
     * Blocking execute batch calls.
     *
     * @param calls to make
     * @return List of responses, in the same order as calls
     */
    public TetherjResponse<List<Object[]>> makeCalls(final List<EthCall> calls) {

        List<Future<TetherjResponse<Object[]>>> futures = new ArrayList<>();
        List<Object[]> responses = new ArrayList<>();
        for (EthCall call : calls) {
            futures.add(makeCallFuture(call));
        }

        for (Future<TetherjResponse<Object[]>> future : futures) {
            TetherjResponse<Object[]> response;
            try {
                response = future.get(1500, TimeUnit.MILLISECONDS);

                if (response.getErrorType() != null) {
                    return new TetherjResponse<>(response);
                }

                responses.add(response.getValue());
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                return new TetherjResponse<>(ErrorType.BLOCKCHAIN_CLIENT_BAD_CONNECTION, ex);
            }
        }

        return new TetherjResponse<List<Object[]>>(null, null, responses);
    }

    /**
     * Async compile solidity.
     *
     * @param sourceCode to compiled
     * @param callable   with compile output response
     */
    public void compileSolidity(String sourceCode, TetherjHandle<CompileOutput> callable) {
        performAsyncRpcAction(new RpcAction<CompileOutput>() {

            @Override
            public CompileOutput call() {
                return rpc.compileSolidity(sourceCode);
            }

        }, callable);
    }

    /**
     * Blocking compile solidity.
     *
     * @param sourceCode to compile
     * @return compile output response
     */
    public TetherjResponse<CompileOutput> compileSolidity(String sourceCode) {
        return performBlockingRpcAction(new RpcAction<CompileOutput>() {

            @Override
            public CompileOutput call() {
                return rpc.compileSolidity(sourceCode);
            }
        });
    }

    /**
     * Future compile solidity.
     *
     * @param sourceCode to compile
     * @return future for compile output response
     */
    public Future<TetherjResponse<CompileOutput>> compileSolidityFuture(String sourceCode) {
        return performFutureRpcAction(new RpcAction<CompileOutput>() {

            @Override
            public CompileOutput call() {
                return rpc.compileSolidity(sourceCode);
            }
        });
    }

    /**
     * Async get a transaction by transaction hash.
     *
     * @param txHash   to get data by.
     * @param callable with Transaction response
     */
    public void getTransaction(String txHash, TetherjHandle<Transaction> callable) {
        performAsyncRpcAction(new RpcAction<Transaction>() {

            @Override
            public Transaction call() {
                return rpc.getTransaction(txHash);
            }

        }, callable);
    }

    /**
     * Blocking get a transaction by transaction hash.
     *
     * @param txHash to get data by.
     * @return with Transaction response
     */
    public TetherjResponse<Transaction> getTransaction(String txHash) {
        return performBlockingRpcAction(new RpcAction<Transaction>() {

            @Override
            public Transaction call() {
                return rpc.getTransaction(txHash);
            }
        });
    }

    /**
     * Future get a transaction by transaction hash.
     *
     * @param txHash to get transaction data by.
     * @return future for Transaction response
     */
    public Future<TetherjResponse<Transaction>> getTransactionFuture(String txHash) {
        return performFutureRpcAction(new RpcAction<Transaction>() {

            @Override
            public Transaction call() {
                return rpc.getTransaction(txHash);
            }
        });
    }

    /**
     * Async get the latest block.
     *
     * @param callable with Block response
     */
    public void getLatestBlock(TetherjHandle<Block> callable) {
        performAsyncRpcAction(new RpcAction<Block>() {

            @Override
            public Block call() {
                return rpc.getLatestBlock();
            }

        }, callable);
    }

    /**
     * Blocking get the latest block.
     *
     * @return block data
     */
    public TetherjResponse<Block> getLatestBlock() {
        return performBlockingRpcAction(new RpcAction<Block>() {

            @Override
            public Block call() {
                return rpc.getLatestBlock();
            }
        });
    }

    /**
     * Future get the latest block.
     *
     * @return block data
     */
    public Future<TetherjResponse<Block>> getLatestBlockFuture() {
        return performFutureRpcAction(new RpcAction<Block>() {

            @Override
            public Block call() {
                return rpc.getLatestBlock();
            }
        });
    }

    /**
     * Async get the transaction receipt.
     *
     * @param txHash   to receipt transaction by.
     * @param callable with TransactionReceipt response
     */
    public void getTransactionReceipt(String txHash, TetherjHandle<TransactionReceipt> callable) {
        performAsyncRpcAction(new RpcAction<TransactionReceipt>() {

            @Override
            public TransactionReceipt call() {
                return rpc.getTransactionReceipt(txHash);
            }

        }, callable);
    }

    /**
     * Blocking get the transaction receipt.
     *
     * @param txHash to receipt transaction by.
     */
    public TetherjResponse<TransactionReceipt> getTransactionReceipt(String txHash) {
        return performBlockingRpcAction(new RpcAction<TransactionReceipt>() {

            @Override
            public TransactionReceipt call() {
                return rpc.getTransactionReceipt(txHash);
            }
        });
    }

    /**
     * Future get the transaction receipt.
     *
     * @param txHash to receipt transaction by.
     */
    public Future<TetherjResponse<TransactionReceipt>> getTransactionReceiptFuture(String txHash) {
        return performFutureRpcAction(new RpcAction<TransactionReceipt>() {

            @Override
            public TransactionReceipt call() {
                return rpc.getTransactionReceipt(txHash);
            }
        });
    }

    /**
     * Async create filter.
     *
     * @param callable with Block response
     */
    public void newFilter(TetherjHandle<BigInteger> callable) {
        performAsyncRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newFilter());
            }

        }, callable);
    }

    /**
     * Async create filter with custom request.
     *
     * @param request  for filter
     * @param callable with Block response
     */
    public void newFilter(FilterLogRequest request, TetherjHandle<BigInteger> callable) {
        performAsyncRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newFilter(request));
            }

        }, callable);
    }

    /**
     * Blocking create new filter.
     */
    public TetherjResponse<BigInteger> newFilter() {
        return performBlockingRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newFilter());
            }
        });
    }

    /**
     * Blocking create new filter with custom request.
     *
     * @param request for filter
     * @return response for filter id
     */
    public TetherjResponse<BigInteger> newFilter(FilterLogRequest request) {
        return performBlockingRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newFilter(request));
            }
        });
    }

    /**
     * Future create new filter.
     *
     * @return future to get response for filter id
     */
    public Future<TetherjResponse<BigInteger>> newFilterFuture() {
        return performFutureRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newFilter());
            }
        });
    }

    /**
     * Future create new filter with custom request.
     *
     * @param request for filter
     * @return future to get response for filter id
     */
    public Future<TetherjResponse<BigInteger>> newFilterFuture(FilterLogRequest request) {
        return performFutureRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newFilter(request));
            }
        });
    }

    /**
     * Async create pending transaction filter.
     *
     * @param callable with Block response
     */
    public void newPendingTransactionFilter(TetherjHandle<BigInteger> callable) {
        performAsyncRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newPendingTransactionFilter());
            }

        }, callable);
    }

    /**
     * Blocking create new pending transaction filter.
     */
    public TetherjResponse<BigInteger> newPendingTransactionFilter() {
        return performBlockingRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newPendingTransactionFilter());
            }
        });
    }

    /**
     * Future create new pending transaction filter.
     *
     * @return future to get response for filter id
     */
    public Future<TetherjResponse<BigInteger>> newPendingTransactionFilterFuture() {
        return performFutureRpcAction(new RpcAction<BigInteger>() {

            @Override
            public BigInteger call() {
                return CryptoUtil.hexToBigInteger(rpc.newPendingTransactionFilter());
            }
        });
    }

    /**
     * Async remove filter.
     *
     * @param filterId to uninstall
     * @param callable with Block response
     */
    public void uninstallFilter(BigInteger filterId, TetherjHandle<Boolean> callable) {
        performAsyncRpcAction(new RpcAction<Boolean>() {

            @Override
            public Boolean call() {
                return rpc.uninstallFilter(filterId);
            }

        }, callable);
    }

    /**
     * Blocking remove filter.
     *
     * @param filterId to uninstall
     * @return response for uninstall success
     */
    public TetherjResponse<Boolean> uninstallFilter(BigInteger filterId) {
        return performBlockingRpcAction(new RpcAction<Boolean>() {

            @Override
            public Boolean call() {
                return rpc.uninstallFilter(filterId);
            }
        });
    }

    /**
     * Future remove filter.
     *
     * @param filterId to uninstall
     * @return future to get uninstall success
     */
    public Future<TetherjResponse<Boolean>> uninstallFilterFuture(BigInteger filterId) {
        return performFutureRpcAction(new RpcAction<Boolean>() {

            @Override
            public Boolean call() {
                return rpc.uninstallFilter(filterId);
            }
        });
    }

    /**
     * Async get filter changes.
     *
     * @param filterId to get changes for
     * @param callable with Block response
     */
    public void getFilterChanges(BigInteger filterId,
        TetherjHandle<List<FilterLogObject>> callable) {
        performAsyncRpcAction(new RpcAction<List<FilterLogObject>>() {

            @Override
            public List<FilterLogObject> call() {
                return rpc.getFilterChanges(filterId);
            }

        }, callable);
    }

    /**
     * Blocking get filter changes.
     *
     * @param filterId to get changes for
     * @return response for change objects
     */
    public TetherjResponse<List<FilterLogObject>> getFilterChanges(BigInteger filterId) {
        return performBlockingRpcAction(new RpcAction<List<FilterLogObject>>() {

            @Override
            public List<FilterLogObject> call() {
                return rpc.getFilterChanges(filterId);
            }
        });
    }

    /**
     * Future get filter changes.
     *
     * @param filterId to get changes for
     * @return future to get uninstall success
     */
    public Future<TetherjResponse<List<FilterLogObject>>> getFilterChangesFuture(
        BigInteger filterId) {
        return performFutureRpcAction(new RpcAction<List<FilterLogObject>>() {

            @Override
            public List<FilterLogObject> call() {
                return rpc.getFilterChanges(filterId);
            }
        });
    }

    /**
     * Async get filter logs.
     *
     * @param filterId to get logs for
     * @param callable with Block response
     */
    public void getFilterLogs(BigInteger filterId, TetherjHandle<List<FilterLogObject>> callable) {
        performAsyncRpcAction(new RpcAction<List<FilterLogObject>>() {

            @Override
            public List<FilterLogObject> call() {
                return rpc.getFilterLogs(filterId);
            }

        }, callable);
    }

    /**
     * Blocking get filter logs.
     *
     * @param filterId to get logs for
     * @return response for change objects
     */
    public TetherjResponse<List<FilterLogObject>> getFilterLogs(BigInteger filterId) {
        return performBlockingRpcAction(new RpcAction<List<FilterLogObject>>() {

            @Override
            public List<FilterLogObject> call() {
                return rpc.getFilterLogs(filterId);
            }
        });
    }

    /**
     * Future get filter logs.
     *
     * @param filterId to get logs for
     * @return future to get uninstall success
     */
    public Future<TetherjResponse<List<FilterLogObject>>> getFilterLogsFuture(BigInteger filterId) {
        return performFutureRpcAction(new RpcAction<List<FilterLogObject>>() {

            @Override
            public List<FilterLogObject> call() {
                return rpc.getFilterLogs(filterId);
            }
        });
    }

    /**
     * Async get filter changes for pending transactions.
     *
     * @param filterId to get changes for
     * @param callable with Block response
     */
    public void getPendingTransactionFilterChanges(BigInteger filterId,
        TetherjHandle<List<String>> callable) {
        performAsyncRpcAction(new RpcAction<List<String>>() {

            @Override
            public List<String> call() {
                return rpc.getPendingTransactionFilterChanges(filterId);
            }

        }, callable);
    }

    /**
     * Blocking get filter changes for pending transactions.
     *
     * @param filterId to get changes for
     * @return response for change objects
     */
    public TetherjResponse<List<String>> getPendingTransactionFilterChanges(BigInteger filterId) {
        return performBlockingRpcAction(new RpcAction<List<String>>() {

            @Override
            public List<String> call() {
                return rpc.getPendingTransactionFilterChanges(filterId);
            }
        });
    }

    /**
     * Future get filter changes for pending transactions.
     *
     * @param filterId to get changes for
     * @return future to get uninstall success
     */
    public Future<TetherjResponse<List<String>>> getPendingTransactionFilterChangesFuture(
        BigInteger filterId) {
        return performFutureRpcAction(new RpcAction<List<String>>() {

            @Override
            public List<String> call() {
                return rpc.getPendingTransactionFilterChanges(filterId);
            }
        });
    }

    /**
     * Blocking get all events using a query request. Warning: might take a lot if request block
     * range is large (over 100000 blocks)
     *
     * @param request
     * @return
     */
    public TetherjResponse<List<EthEvent>> getEvents(FilterLogRequest request) {
        TetherjResponse<BigInteger> filterResponse = this.newFilter(request);

        List<EthEvent> events = new ArrayList<>();
        if (filterResponse.isSuccessful()) {
            TetherjResponse<List<FilterLogObject>> eventResponse = this
                .getFilterLogs(filterResponse.getValue());
            this.uninstallFilter(filterResponse.getValue());

            if (eventResponse.isSuccessful() && eventResponse.getValue() != null) {
                for (FilterLogObject obj : eventResponse.getValue()) {
                    EthEvent event = new EthEvent();
                    event.setData(request.decodeEventData(obj.getData(), obj.getTopics()));
                    event.setFilterLogObject(obj);
                    events.add(event);
                }
                return new TetherjResponse<>(null, null, events);
            } else if (!eventResponse.isSuccessful()) {
                return new TetherjResponse<>(filterResponse);
            }
        }

        return new TetherjResponse<>(filterResponse);
    }

    /**
     * Async get all events using a query request. Warning: might take a lot if request block
     * range is large (over 100000 blocks)
     *
     * @param request
     * @param handle  to call after getting event list
     * @return
     */
    public void getEvents(FilterLogRequest request, TetherjHandle<List<EthEvent>> handle) {
        this.getLatestBlockNumber(latestBlockResponse -> {
            if (!latestBlockResponse.isSuccessful()) {
                handle.call(new TetherjResponse<>(latestBlockResponse));
            } else {

                FilterLogRequest partialRequest = request;
                BigInteger fromBlock = blockAsBigInteger(request.getFromBlock(),
                    latestBlockResponse.getValue());
                BigInteger toBlock = blockAsBigInteger(request.getToBlock(), latestBlockResponse
                    .getValue());

                if (toBlock.subtract(fromBlock).compareTo(BigInteger.valueOf
                    (ASYNC_FILTER_BLOCK_SPLIT)) > 0) {
                    toBlock = fromBlock.add(BigInteger.valueOf(ASYNC_FILTER_BLOCK_SPLIT));

                    partialRequest = new FilterLogRequest();
                    partialRequest.setAddress(request.getAddress());
                    partialRequest.setFromBlock(fromBlock);
                    partialRequest.setToBlock(toBlock);
                    partialRequest.setTopics(request.getTopics());
                    partialRequest.setFunction(request.getFunction());
                }

                List<EthEvent> eventResponse = new ArrayList<EthEvent>();
                getEventsPartial(eventResponse, latestBlockResponse.getValue(), partialRequest,
                    request, handle);
            }
        });
    }

    private void getEventsPartial(List<EthEvent> eventResponse, BigInteger latestBlock,
        FilterLogRequest partialRequest, FilterLogRequest request, TetherjHandle<List<EthEvent>>
        handle) {
        TetherjResponse<List<EthEvent>> partialEvents = getEvents(partialRequest);
        if (!partialEvents.isSuccessful()) {
            handle.call(new TetherjResponse<>(partialEvents));
        } else {
            eventResponse.addAll(partialEvents.getValue());

            BigInteger partialToBlock = blockAsBigInteger(partialRequest.getToBlock(), latestBlock);
            BigInteger requestToBlock = blockAsBigInteger(request.getToBlock(), latestBlock);
            if (partialToBlock.compareTo(requestToBlock) < 0) {
                BigInteger newFromBlock = partialToBlock.add(BigInteger.ONE);
                partialRequest.setFromBlock(newFromBlock);

                if (requestToBlock.subtract(newFromBlock).compareTo(BigInteger.valueOf
                    (ASYNC_FILTER_BLOCK_SPLIT)) > 0) {
                    partialRequest.setToBlock(newFromBlock.add(BigInteger.valueOf
                        (ASYNC_FILTER_BLOCK_SPLIT)));
                } else {
                    partialRequest.setToBlock(requestToBlock);
                }

                executorAsync(() -> getEventsPartial(eventResponse, latestBlock, partialRequest,
                    request, handle));

            } else {
                handle.call(new TetherjResponse<>(null, null, eventResponse));
            }
        }
    }

    private BigInteger blockAsBigInteger(String block, BigInteger latestBlock) {
        switch (block) {
            case "earliest":
                return BigInteger.ZERO;
            case "latest":
            case "pending":
                return latestBlock;
            default:
                return CryptoUtil.hexToBigInteger(block);
        }
    }

    /**
     * Create a watch for events of a request type. The watch will notify of any new events, the
     * watch can be cancelled at any time using the event below.
     *
     * @param request     request describing event query
     * @param eventHandle the handle to call when new events are found
     * @return watch handle, can be used to cancel watch.
     */
    public TetherjResponse<TetherjFilterWatch> watchEvents(FilterLogRequest request,
        TetherjHandle<List<EthEvent>> eventHandle) {

        TetherjResponse<BigInteger> filterResponse = this.newFilter(request);

        if (!filterResponse.isSuccessful()) {
            return new TetherjResponse<>(filterResponse);
        }

        BigInteger filterId = filterResponse.getValue();
        TetherjFilterWatch watch = new TetherjFilterWatch(eventHandle);

        executorAsync(() -> watchEventChanges(request, watch, filterId, eventHandle));

        return new TetherjResponse<>(null, null, watch);
    }

    private void watchEventChanges(FilterLogRequest request, TetherjFilterWatch watch, BigInteger
        filterId,
        TetherjHandle<List<EthEvent>> eventHandle) {
        if (watch.getIsCancelled().get()) {
            uninstallFilter(filterId, response -> {
            });
        } else {
            List<EthEvent> events = new ArrayList<>();
            TetherjResponse<List<FilterLogObject>> eventResponse = this
                .getFilterChanges(filterId);

            if (eventResponse.isSuccessful() && eventResponse.getValue() != null) {
                for (FilterLogObject obj : eventResponse.getValue()) {
                    EthEvent event = new EthEvent();
                    event.setData(request.decodeEventData(obj.getData(), obj.getTopics()));
                    event.setFilterLogObject(obj);
                    events.add(event);
                }
                eventHandle.call(new TetherjResponse<>(null, null, events));
            } else if (!eventResponse.isSuccessful()) {
                logger.error("Event Watch with filterId " + filterId + " for request " + request
                    .toString() + " failed when fetching! ERROR: " + eventResponse.getException()
                    .getMessage());
            }

            executorAsync(() -> watchEventChanges(request, watch, filterId, eventHandle));
        }
    }
}
