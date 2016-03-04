package com.cegeka.tetherj.api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cegeka.tetherj.EthCall;
import com.cegeka.tetherj.EthRpcClient;
import com.cegeka.tetherj.EthSignedTransaction;
import com.cegeka.tetherj.EthTransaction;
import com.cegeka.tetherj.EthWallet;
import com.cegeka.tetherj.pojo.Block;
import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.Transaction;
import com.cegeka.tetherj.pojo.TransactionReceipt;
import com.googlecode.jsonrpc4j.HttpException;
import com.googlecode.jsonrpc4j.JsonRpcClientException;

/**
 * Implementation for an Ethereum service api
 * 
 * @author Andrei Grigoriu
 *
 */
public class EthereumService {

	public final static int defaultExecutorThreads = 2;

	/**
	 * A generic wrapper for a rpc call.
	 * 
	 * @author Andrei Grigoriu
	 *
	 * @param <T>
	 *            return type of rpc call
	 */
	public interface RpcAction<T> {

		/**
		 * Execute rpc call here
		 * 
		 * @return rpc response
		 */
		public T call();
	}

	/**
	 * Interval between receipt check polls
	 */
	public static final int receiptCheckIntervalMillis = 1000;

	/**
	 * Max receipt checks to do
	 */
	public static final int receiptMaxChecks = 60 * 1000 * 10 / receiptCheckIntervalMillis;

	private final EthRpcClient rpc;
	private final ScheduledExecutorService executor;

	private static final Logger logger = LogManager.getLogger(EthereumService.class);

	/**
	 * Creates executor automatically
	 */
	public EthereumService() {
		this(EthereumService.defaultExecutorThreads, EthRpcClient.defaultHostname, EthRpcClient.defaultPort);
	}

	/**
	 * Creates executor with custom number of threads.
	 * 
	 * @param executorThreads
	 *            to spawn
	 */
	public EthereumService(int executorThreads) {
		this(executorThreads, EthRpcClient.defaultHostname, EthRpcClient.defaultPort);
	}

	/**
	 * Creates custom number of threads and custom ethereum client connection
	 * info.
	 * 
	 * @param executorThreads
	 *            to spawn
	 * @param rpcHostname
	 *            of the ethereum client
	 * @param port
	 *            of the ethereum client
	 */
	public EthereumService(int executorThreads, String rpcHostname, int port) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(executorThreads, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);

				t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						handleUnknownThrowables(e);
					}
				});

				return t;
			}
		});

		this.executor = executor;
		rpc = new EthRpcClient(rpcHostname, port);
		logger.info("Created ethereum service");
	}

	/**
	 * 
	 * @param executor
	 *            to use for async and future calls, also for polling
	 */
	public EthereumService(ScheduledExecutorService executor) {
		this(executor, EthRpcClient.defaultHostname, EthRpcClient.defaultPort);
	}

	/**
	 * 
	 * @param executor
	 *            to use for async and future calls, also for polling
	 * @param rpcHostname
	 *            ethereum client hostname
	 */
	public EthereumService(ScheduledExecutorService executor, String rpcHostname) {
		this(executor, rpcHostname, EthRpcClient.defaultPort);
	}

	/**
	 * 
	 * @param executor
	 *            to use for async and future calls, also for polling
	 * @param rpcHostname
	 *            ethereum client hostname
	 * @param port
	 *            ethereum client port
	 */
	public EthereumService(ScheduledExecutorService executor, String rpcHostname, int port) {
		this.executor = executor;
		rpc = new EthRpcClient(rpcHostname, port);
		logger.info("Created ethereum service");
	}

	/**
	 * Call this when you don't know what to do with e
	 * 
	 * @param e
	 *            the exception thrown somewhere
	 */
	private void handleUnknownThrowables(Throwable e) {
		if (e != null) {

			StringWriter esw = new StringWriter();
			PrintWriter epw = new PrintWriter(esw);
			e.printStackTrace(epw);
			String eStack = esw.toString();

			String message = "Tetherj uncaught exception: " + e.toString() + " " + e.getMessage() + " at \n"
					+ eStack;

			if (e.getCause() != null) {
				StringWriter csw = new StringWriter();
				PrintWriter cpw = new PrintWriter(csw);
				Throwable c = e.getCause();
				c.printStackTrace(cpw);
				String cStack = csw.toString();

				message += "\n CAUSE: " + c.toString() + " " + c.getMessage() + " at \n" + cStack;
			}

			logger.error(message);
		}
	}

	/**
	 * Generate wallet with a random key pair.
	 * 
	 * @param passphrase
	 * @return
	 */
	public EthWallet createWallet(String passphrase) {
		return EthWallet.createWallet(passphrase);
	}

	/**
	 * Get internal rpc client used to communicate with the ethereum client
	 * 
	 * @return rpc client
	 */
	public EthRpcClient getRpcClient() {
		return rpc;
	}

	/**
	 * Blocking execute of rpc action. Wraps errors into a tetherj response.
	 * 
	 * @param rpcAction
	 * @return response
	 */
	private <T> TetherjResponse<T> performBlockingRpcAction(RpcAction<T> rpcAction) {
		ErrorType err = null;
		Exception e = null;
		T rpcResponse = null;

		try {
			rpcResponse = rpcAction.call();
		} catch (JsonRpcClientException rpcEx) {
			err = ErrorType.BLOCKCHAIN_CLIENT_OPERATION_ERROR;
			e = rpcEx;
		} catch (UndeclaredThrowableException | HttpException ex) {
			err = ErrorType.BLOCKCHAIN_CLIENT_BAD_CONNECTION;
			e = ex;
		} catch (Exception generalException) {
			err = ErrorType.UNKNOWN_ERROR;
			e = generalException;
		}

		return new TetherjResponse<T>(err, e, rpcResponse);
	}

	/**
	 * Async execute of rpc action. Runs callable handle when done.
	 * 
	 * @param rpcAction
	 *            to execute
	 * @param callable
	 *            to execute after rpcAction operation ends
	 */
	private <T> void performAsyncRpcAction(RpcAction<T> rpcAction, TetherjHandle<T> callable) {
		synchronized (executor) {
			try {
				executor.submit(new Runnable() {

					@Override
					public void run() {
						try {
							callable.call(performBlockingRpcAction(rpcAction));
						} catch (Throwable t) {
							handleUnknownThrowables(t);
						}
					}
				});
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Async execute of rpc action. Returns a future to get when operation ends.
	 * 
	 * @param rpcAction
	 *            to execute
	 */
	private <T> Future<TetherjResponse<T>> performFutureRpcAction(RpcAction<T> rpcAction) {
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
	}

	/**
	 * Async get accounts registered in the ethereum client.
	 * 
	 * @param callable
	 *            to call after the accounts are fetched
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
	 * Async get balance of an account.
	 * 
	 * @param address
	 *            to get balance of.
	 * @param callable
	 *            to execute when balance is fetched
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
	 * Blocking get balance of an account
	 * 
	 * @param address
	 *            to get balance of
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
	 * @param address
	 *            to get balance of
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
	 * Async get account nonce of address (does not currently count pending
	 * executions)
	 * 
	 * @param address
	 *            to get account nonce of.
	 * @param callable
	 *            to execute with nonce response
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
	 * Blocking get account nonce of address (does not currently count pending
	 * executions)
	 * 
	 * @param address
	 *            to get account nonce of.
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
	 * Future get account nonce of address (does not currently count pending
	 * executions)
	 * 
	 * @param address
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
	 * Blocking send transaction. Generates nonce automatically (by rpc)
	 * 
	 * @param from
	 *            wallet to sign transaction with
	 * @param transaction
	 *            to send
	 * @return response for transaction hash
	 * @throws WalletLockedException
	 */
	public TetherjResponse<String> sendTransaction(EthWallet from, EthTransaction transaction)
			throws WalletLockedException {
		TetherjResponse<BigInteger> nonceResponse = getAccountNonce(from.getAddress());

		if (nonceResponse.getErrorType() == null) {
			return sendTransaction(from, transaction, nonceResponse.getValue());
		}

		return new TetherjResponse<String>(nonceResponse);
	}

	/**
	 * Blocking send transaction.
	 * 
	 * @param from
	 *            wallet to sign transaction with
	 * @param transaction
	 *            to send
	 * @param nonce
	 *            to sign transaction with
	 * @return response for transaction hash
	 * @throws WalletLockedException
	 */
	public TetherjResponse<String> sendTransaction(EthWallet from, EthTransaction transaction, BigInteger nonce)
			throws WalletLockedException {
		EthSignedTransaction txSigned = transaction.signWithWallet(from, nonce);
		byte[] rawEncoded = txSigned.getSignedEncodedData();

		return performBlockingRpcAction(new RpcAction<String>() {

			@Override
			public String call() {
				logger.debug("Sending transaction {from:" + from.getAddress() + ", nonce: " + nonce + " "
						+ transaction.toString());
				return rpc.sendRawTransaction(rawEncoded);
			}
		});
	}

	/**
	 * Future send transaction.
	 * 
	 * @param from
	 *            wallet to sign transaction with
	 * @param transaction
	 *            to send
	 * @param nonce
	 *            to sign transaction with
	 * @return future to get response for transaction hash.
	 * @throws WalletLockedException
	 */
	public Future<TetherjResponse<String>> sendTransactionFuture(EthWallet from, EthTransaction transaction,
			BigInteger nonce) throws WalletLockedException {
		EthSignedTransaction txSigned = transaction.signWithWallet(from, nonce);
		byte[] rawEncoded = txSigned.getSignedEncodedData();

		return performFutureRpcAction(new RpcAction<String>() {

			@Override
			public String call() {
				logger.debug("Sending transaction {from:" + from.getAddress() + ", nonce: " + nonce + " "
						+ transaction.toString());
				return rpc.sendRawTransaction(rawEncoded);
			}
		});
	}

	/**
	 * Async send transaction. Nonce gets generated automatically via rpc.
	 * 
	 * @param from
	 *            wallet to sign transaction with
	 * @param transaction
	 *            to send
	 * @param callable
	 *            to execute when transaction was submitted.
	 */
	public void sendTransaction(EthWallet from, EthTransaction transaction, TetherjHandle<String> callable) {
		String address = from.getAddress();
		getAccountNonce(address, new TetherjHandle<BigInteger>() {

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
	 * @param from
	 *            wallet to sign transaction with
	 * @param transaction
	 *            to send
	 * @param nonce
	 *            to sign transaction with
	 * @param callable
	 *            to execute when transaction was submitted.
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

		} catch (WalletLockedException e) {
			callable.call(new TetherjResponse<String>(ErrorType.BAD_STATE, e));
		}
	}

	/**
	 * Blocking sign transaction
	 * 
	 * @param transaction
	 *            signed transaction to send
	 * @return response for signed transaction
	 * @throws WalletLockedException
	 */
	public TetherjResponse<EthSignedTransaction> signTransaction(EthTransaction transaction, EthWallet wallet)
			throws WalletLockedException {
		String from = wallet.getAddress();
		TetherjResponse<BigInteger> nonceResponse = getAccountNonce(from);

		if (nonceResponse.getErrorType() != null) {
			return new TetherjResponse<EthSignedTransaction>(nonceResponse);
		}

		EthSignedTransaction txSigned = transaction.signWithWallet(wallet, nonceResponse.getValue());
		return new TetherjResponse<EthSignedTransaction>(null, null, txSigned);
	}

	/**
	 * Sign transaction
	 * 
	 * @param transaction
	 *            signed transaction to send
	 * @return the signed transaction
	 * @throws WalletLockedException
	 */
	public EthSignedTransaction signTransaction(EthTransaction transaction, EthWallet wallet, BigInteger nonce)
			throws WalletLockedException {
		return transaction.signWithWallet(wallet, nonce);
	}

	/**
	 * Blocking send signed transaction.
	 * 
	 * @param transaction
	 *            signed transaction to send
	 * @return response for transaction hash
	 * @throws WalletLockedException
	 */
	public TetherjResponse<String> sendSignedTransaction(EthSignedTransaction transaction) {

		return performBlockingRpcAction(new RpcAction<String>() {

			@Override
			public String call() {
				logger.debug("Sending transaction {from:" + transaction.getFrom() + " " + transaction.toString());
				return rpc.sendRawTransaction(transaction.getSignedEncodedData());
			}
		});
	}

	/**
	 * Async send signed transaction.
	 * 
	 * @param transaction
	 *            signed transaction to send
	 * @param callable
	 *            to execute when transaction was submitted.
	 */
	public void sendSignedTransaction(EthSignedTransaction transaction, TetherjHandle<String> callable) {

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
	 * @param transaction
	 *            signed transaction to send
	 * @param callable
	 *            to execute when transaction was submitted.
	 */
	public Future<TetherjResponse<String>> sendSignedTransactionFuture(EthSignedTransaction transaction) {

		return performFutureRpcAction(new RpcAction<String>() {

			@Override
			public String call() {
				return rpc.sendRawTransaction(transaction.getSignedEncodedData());
			}

		});
	}

	/**
	 * Async listen for tx receipt. Will call when transaction is mined or was
	 * already mined.
	 * 
	 * @param txHash
	 *            transaction hash to listen for
	 * @param secondsTimeout
	 *            seconds until you want give up listening
	 * @param callable
	 *            to execute when transaction is mined
	 */
	public void listenForTxReceipt(final String txHash, int secondsTimeout,
			final TetherjHandle<TransactionReceipt> callable) {
		int checks = secondsTimeout * 1000 / receiptCheckIntervalMillis;
		listenForTxReceipt(txHash, receiptCheckIntervalMillis, checks, callable);
	}

	/**
	 * Async listen for tx receipt. Will call when transaction is mined or was
	 * already mined.
	 * 
	 * @param txHash
	 *            transaction hash to listen for
	 * @param callable
	 *            to execute when transaction is mined
	 */
	public void listenForTxReceipt(final String txHash, final TetherjHandle<TransactionReceipt> callable) {
		listenForTxReceipt(txHash, receiptCheckIntervalMillis, receiptMaxChecks, callable);
	}

	private void listenForTxReceipt(final String txHash, final int checkIntervalMillis, final int checks,
			final TetherjHandle<TransactionReceipt> callable) {

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
						callable.call(new TetherjResponse<TransactionReceipt>(ErrorType.OPERATION_TIMEOUT,
								new TxReceiptTimeoutException()));
					} else {
						synchronized (executor) {
							executor.schedule(new Runnable() {

								@Override
								public void run() {
									listenForTxReceipt(txHash, checkIntervalMillis, checks - 1, callable);
								}
							}, checkIntervalMillis, TimeUnit.MILLISECONDS);
						}
					}
				}
			}
		});
	}

	/**
	 * Async calls and fetches the output of an ethereum function.
	 * 
	 * @param call
	 *            to execute on the ethereum chain
	 * @param callable
	 *            to execute with output data.
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
	 * @param call
	 *            to execute on the ethereum chain
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
	 * @param call
	 *            to execute on the ethereum chain
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
	 * Async compile solidity.
	 * 
	 * @param sourceCode
	 *            to compiled
	 * @param callable
	 *            with compile output response
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
	 * @param sourceCode
	 *            to compile
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
	 * @param sourceCode
	 *            to compile
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
	 * @param txHash
	 *            to get data by.
	 * @param callable
	 *            with Transaction response
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
	 * @param txHash
	 *            to get data by.
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
	 * @param sourceCode
	 *            to get data by.
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
	 * @param callable
	 *            with Block response
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
	 * @param callable
	 *            with Block response
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
	 * @param callable
	 *            with Block response
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
	 * @param txHash
	 *            to receipt transaction by.
	 * @param callable
	 *            with TransactionReceipt response
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
	 * @param txHash
	 *            to receipt transaction by.
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
	 * @param txHash
	 *            to receipt transaction by.
	 */
	public Future<TetherjResponse<TransactionReceipt>> getTransactionReceiptFuture(String txHash) {
		return performFutureRpcAction(new RpcAction<TransactionReceipt>() {

			@Override
			public TransactionReceipt call() {
				return rpc.getTransactionReceipt(txHash);
			}
		});
	}
}
