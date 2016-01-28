package com.cegeka.blocklinks.api;

import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cegeka.blocklinks.ethereum.EthCall;
import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthTransaction;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.pojo.CompileOutput;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;
import com.googlecode.jsonrpc4j.HttpException;
import com.googlecode.jsonrpc4j.JsonRpcClientException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Implementation for an Ethereum service api
 * 
 * @author Andrei Grigoriu
 *
 */
public class EthereumService {

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
	 * Blocking execute of rpc action. Wraps errors into a blocklinks response.
	 * 
	 * @param rpcAction
	 * @return response
	 */
	private <T> BlocklinksResponse<T> performBlockingRpcAction(RpcAction<T> rpcAction) {
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

		return new BlocklinksResponse<T>(err, e, rpcResponse);
	}

	/**
	 * Async execute of rpc action. Runs callable handle when done.
	 * 
	 * @param rpcAction
	 *            to execute
	 * @param callable
	 *            to execute after rpcAction operation ends
	 */
	private <T> void performAsyncRpcAction(RpcAction<T> rpcAction, BlocklinksCallable<T> callable) {
		synchronized (executor) {
			try {
				executor.submit(new Runnable() {

					@Override
					public void run() {
						callable.call(performBlockingRpcAction(rpcAction));
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
	private <T> Future<BlocklinksResponse<T>> performFutureRpcAction(RpcAction<T> rpcAction) {
		synchronized (executor) {
			try {
				return executor.submit(new Callable<BlocklinksResponse<T>>() {

					@Override
					public BlocklinksResponse<T> call() throws Exception {
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
	public void getAccounts(BlocklinksCallable<String[]> callable) {
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
	public BlocklinksResponse<String[]> getAccounts() {
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
	public Future<BlocklinksResponse<String[]>> getAccountsFuture() {
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
	public void getBalance(final String address, BlocklinksCallable<BigInteger> callable) {
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
	public BlocklinksResponse<BigInteger> getBalance(final String address) {
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
	public Future<BlocklinksResponse<BigInteger>> getBalanceFuture(final String address) {
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
	public void getAccountNonce(final String address, BlocklinksCallable<BigInteger> callable) {
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
	public BlocklinksResponse<BigInteger> getAccountNonce(final String address) {
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
	public Future<BlocklinksResponse<BigInteger>> getAccountNonceFuture(final String address) {
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
	public BlocklinksResponse<String> sendTransaction(EthWallet from, EthTransaction transaction)
			throws WalletLockedException {
		BlocklinksResponse<BigInteger> nonceResponse = getAccountNonce(from.getAddress());

		if (nonceResponse.getErrorType() == null) {
			return sendTransaction(from, transaction, nonceResponse.getValue());
		}

		return new BlocklinksResponse<String>(nonceResponse);
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
	public BlocklinksResponse<String> sendTransaction(EthWallet from, EthTransaction transaction, BigInteger nonce)
			throws WalletLockedException {
		byte[] rawEncoded = transaction.signWithWallet(from, nonce);

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
	public Future<BlocklinksResponse<String>> sendTransactionFuture(EthWallet from, EthTransaction transaction,
			BigInteger nonce) throws WalletLockedException {
		byte[] rawEncoded = transaction.signWithWallet(from, nonce);

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
	public void sendTransaction(EthWallet from, EthTransaction transaction, BlocklinksCallable<String> callable) {
		String address = from.getAddress();
		getAccountNonce(address, new BlocklinksCallable<BigInteger>() {

			@Override
			public void call(BlocklinksResponse<BigInteger> response) {
				if (response.getErrorType() == null) {
					sendTransaction(from, transaction, response.getValue(), callable);
				} else {
					callable.call(new BlocklinksResponse<>(response));
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
			BlocklinksCallable<String> callable) {
		try {
			byte[] rawEncoded = transaction.signWithWallet(from, nonce);

			performAsyncRpcAction(new RpcAction<String>() {

				@Override
				public String call() {
					return rpc.sendRawTransaction(rawEncoded);
				}

			}, callable);

		} catch (WalletLockedException e) {
			callable.call(new BlocklinksResponse<String>(ErrorType.BAD_STATE, e));
		}
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
	public void listenForTxReceipt(final String txHash, final BlocklinksCallable<TransactionReceipt> callable) {
		listenForTxReceipt(txHash, receiptCheckIntervalMillis, receiptMaxChecks, callable);
	}

	private void listenForTxReceipt(final String txHash, final int checkIntervalMillis, final int checks,
			final BlocklinksCallable<TransactionReceipt> callable) {

		performAsyncRpcAction(new RpcAction<TransactionReceipt>() {

			@Override
			public TransactionReceipt call() {
				TransactionReceipt receipt = rpc.getTransactionReceipt(txHash);
				return receipt;
			}
		}, new BlocklinksCallable<TransactionReceipt>() {

			@Override
			public void call(BlocklinksResponse<TransactionReceipt> response) {
				if (response.getErrorType() != null) {
					callable.call(response);
				} else {
					TransactionReceipt receipt = response.getValue();

					if (receipt != null && receipt.getBlockNumber() != null) {
						callable.call(response);
					} else if (checks <= 0) {
						callable.call(new BlocklinksResponse<TransactionReceipt>(ErrorType.OPERATION_TIMEOUT,
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
	public void makeCall(final EthCall call, BlocklinksCallable<Object[]> callable) {
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
	public BlocklinksResponse<Object[]> makeCall(final EthCall call) {
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
	public Future<BlocklinksResponse<Object[]>> makeCallFuture(final EthCall call) {
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
	 *            with compile ouput response
	 */
	public void compileSolidity(String sourceCode, BlocklinksCallable<CompileOutput> callable) {
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
	public BlocklinksResponse<CompileOutput> compileSolidity(String sourceCode) {
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
	public Future<BlocklinksResponse<CompileOutput>> compileSolidityFuture(String sourceCode) {
		return performFutureRpcAction(new RpcAction<CompileOutput>() {

			@Override
			public CompileOutput call() {
				return rpc.compileSolidity(sourceCode);
			}
		});
	}

}
