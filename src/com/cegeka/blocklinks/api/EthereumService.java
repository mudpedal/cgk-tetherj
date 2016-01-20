package com.cegeka.blocklinks.api;

import java.io.File;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;
import com.googlecode.jsonrpc4j.HttpException;
import com.googlecode.jsonrpc4j.JsonRpcClientException;

/**
 * Implementation for an Ethereum service api
 *
 */
public class EthereumService {
	private interface ReceiptCallback {
		public void success(TransactionReceipt receipt);
		public void fail();
	
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
	
	public BlocklinksResponse<String[]> getAccounts() {
		ErrorTypes err = null;
		Exception e = null;
		String[] rpcResponse = null;
		try {
			rpcResponse = rpc.getAccounts();
		} catch (JsonRpcClientException rpcEx) {
			err = ErrorTypes.BLOCKCHAIN_CLIENT_OPERATION_ERROR;
		} catch (UndeclaredThrowableException | HttpException ex) {
			err = ErrorTypes.BLOCKCHAIN_CLIENT_BAD_CONNECTION;
			e = ex;
		}
		
		return new BlocklinksResponse<String[]>(err, e, rpcResponse);
	}
	
	public void getAccounts(BlocklinksCallable<String[]> callable) {
		synchronized (executor) {
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					callable.call(getAccounts());
				}
			});	
		}
	}

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

}
