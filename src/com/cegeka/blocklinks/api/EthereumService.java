package com.cegeka.blocklinks.api;

import java.io.File;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.util.concurrent.ScheduledExecutorService;

import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.googlecode.jsonrpc4j.HttpException;
import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.sun.istack.internal.logging.Logger;

/**
 * Implementation for an Ethereum service api
 *
 */
public class EthereumService {
	
	public interface RpcAction<T> {
		public T call();
	}

	public static final int receiptCheckIntervalMillis = 1000;
	public static final int receiptMaxChecks = 60 * 1000 * 10 / receiptCheckIntervalMillis;

	private final EthRpcClient rpc;
	private final ScheduledExecutorService executor;

	public EthereumService(ScheduledExecutorService executor, File keystoreDir) {
		this(executor, keystoreDir, EthRpcClient.defaultHostname, EthRpcClient.defaultPort);
	}
	
	public EthereumService(ScheduledExecutorService executor, File keystoreDir, String rpcHostname) {
		this(executor, keystoreDir, rpcHostname, EthRpcClient.defaultPort);
	}
	
	public EthereumService(ScheduledExecutorService executor, File keystoreDir, String rpcHostname, int port) {
		this.executor = executor;
		rpc = new EthRpcClient(rpcHostname, port);
	}
	
	public EthWallet createWallet(String passphrase) {
		return EthWallet.createWallet(passphrase);
	}
	
	public EthRpcClient getRpcClient() {
		return rpc;
	}
	
	private <T> BlocklinksResponse<T> performBlockingRpcAction(RpcAction<T> rpcAction) {
		ErrorType err = null;
		Exception e = null;
		T rpcResponse = null;
		
		try {
			rpcResponse = rpcAction.call();
		} catch (JsonRpcClientException rpcEx) {
			err = ErrorType.BLOCKCHAIN_CLIENT_OPERATION_ERROR;
		} catch (UndeclaredThrowableException | HttpException ex) {
			err = ErrorType.BLOCKCHAIN_CLIENT_BAD_CONNECTION;
			e = ex;
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
	
	public void getAccounts(BlocklinksCallable<String[]> callable) {
		performAsyncRpcAction(new RpcAction<String[]>() {

			@Override
			public String[] call() {
				return rpc.getAccounts();
			}
			
		}, callable);
	}
	
	public BlocklinksResponse<String[]> getAccounts() {
		return performBlockingRpcAction(new RpcAction<String[]>() {

			@Override
			public String[] call() {
				return rpc.getAccounts();
			}
		});
	}
	
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

	/*
	private void listenForTxReceipt(final String txHash, final int checkIntervalMillis, final int checks,
			final ReceiptCallback callback) {

		try {
			synchronized (executor) {
				executor.schedule(new Runnable() {

					public void run() {
						TransactionReceipt receipt = rpc.getTransactionReceipt(txHash);

						if (checks <= 0) {
							callback.fail();
						} else if (receipt != null && receipt.getBlockNumber() != null) {
							callback.success(receipt);
						} else {
							listenForTxReceipt(txHash, checkIntervalMillis, checks - 1, callback);
						}
					}

				}, checkIntervalMillis, TimeUnit.MILLISECONDS);
			}
		} catch (RejectedExecutionException e) {
		}
	}
	*/

}
