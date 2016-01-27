package com.cegeka.blocklinks.api;

import java.io.File;
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
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;
import com.googlecode.jsonrpc4j.HttpException;
import com.googlecode.jsonrpc4j.JsonRpcClientException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Implementation for an Ethereum service api
 *
 */
public class EthereumService {

	/**
	 * A generic wrapper for a rpc call.
	 * @author Andrei Grigoriu
	 *
	 * @param <T> return type of rpc call
	 */
	public interface RpcAction<T> {
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

	/** Constructors
	 * 
	 */
	
	/**
	 * 
	 * @param executor - to use for async and future calls, also for polling
	 */
	public EthereumService(ScheduledExecutorService executor) {
		this(executor, EthRpcClient.defaultHostname, EthRpcClient.defaultPort);
	}

	/**
	 * 
	 * @param executor - to use for async and future calls, also for polling
	 * @param rpcHostname - ethereum client hostname
	 */
	public EthereumService(ScheduledExecutorService executor, String rpcHostname) {
		this(executor, rpcHostname, EthRpcClient.defaultPort);
	}

	/**
	 * 
	 * @param executor - to use for async and future calls, also for polling
	 * @param rpcHostname - ethereum client hostname
	 * @param port - ethereum client port
	 */
	public EthereumService(ScheduledExecutorService executor, String rpcHostname, int port) {
		this.executor = executor;
		rpc = new EthRpcClient(rpcHostname, port);
		logger.info("Created ethereum service");
	}

	/**
	 * Generate wallet with a random key pair.
	 * @param passphrase
	 * @return
	 */
	public EthWallet createWallet(String passphrase) {
		return EthWallet.createWallet(passphrase);
	}

	/**
	 * Get internal rpc client used to communicate with the ethereum client
	 * @return
	 */
	public EthRpcClient getRpcClient() {
		return rpc;
	}

	/* controlled rpc executions (async, future, blocking)
	 * 
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

	/*
	 * getAccounts implementations
	 */
	
	/**
	 * Async get accounts registered in the ethereum client.
	 * @param callable to call after the accounts are fetched
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

	/*
	 * getBalance implementations
	 */
	
	public void getBalance(final String address, BlocklinksCallable<BigInteger> callable) {
		performAsyncRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getBalance(address);
			}

		}, callable);
	}

	public BlocklinksResponse<BigInteger> getBalance(final String address) {
		return performBlockingRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getBalance(address);
			}
		});
	}
	
	public Future<BlocklinksResponse<BigInteger>> getBalanceFuture(final String address) {
		return performFutureRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getBalance(address);
			}
		});
	}

	/*
	 * getAccountNonce implementations
	 */
	
	public void getAccountNonce(final String address, BlocklinksCallable<BigInteger> callable) {
		performAsyncRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getAccountNonce(address);
			}

		}, callable);
	}

	public BlocklinksResponse<BigInteger> getAccountNonce(final String address) {
		return performBlockingRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getAccountNonce(address);
			}
		});
	}
	
	public Future<BlocklinksResponse<BigInteger>> getAccountNonceFuture(final String address) {
		return performFutureRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getAccountNonce(address);
			}
		});
	}

	public BlocklinksResponse<String> sendTransaction(EthWallet from, EthTransaction transaction)
			throws WalletLockedException {
		BlocklinksResponse<BigInteger> nonceResponse = getAccountNonce(from.getAddress());

		if (nonceResponse.getErrorType() == null) {
			return sendTransaction(from, transaction, nonceResponse.getValue());
		}

		return new BlocklinksResponse<String>(nonceResponse);
	}

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
	
	public Future<BlocklinksResponse<String>> sendTransactionFuture(EthWallet from, EthTransaction transaction, BigInteger nonce)
			throws WalletLockedException {
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

	/* 
	 * Calls callable when tx is mined (or is already mined)
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
	
	/*
	 * makeCall implementations
	 */

	public void makeCall(final EthCall call, BlocklinksCallable<Object[]> callable) {
		performAsyncRpcAction(new RpcAction<Object[]>() {

			@Override
			public Object[] call() {
				return call.decodeOutput(rpc.callMethod(call));
			}

		}, callable);
	}

	public BlocklinksResponse<Object[]> makeCall(final EthCall call) {
		return performBlockingRpcAction(new RpcAction<Object[]>() {

			@Override
			public Object[] call() {
				return call.decodeOutput(rpc.callMethod(call));
			}
		});
	}
	
	public Future<BlocklinksResponse<Object[]>> makeCallFuture(final EthCall call) {
		return performFutureRpcAction(new RpcAction<Object[]>() {

			@Override
			public Object[] call() {
				return call.decodeOutput(rpc.callMethod(call));
			}
		});
	}

}
