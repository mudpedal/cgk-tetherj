package com.cegeka.blocklinks.api;

import java.io.File;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;

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

	private final EthRpcClient rpc = new EthRpcClient();
	private final ScheduledExecutorService executor;

	public EthereumService(ScheduledExecutorService executor, File keystoreDir) {
		this.executor = executor;
	}
	
	public EthWallet createWallet(String passphrase) {
		return EthWallet.createWallet(passphrase);
	}
	
	public EthRpcClient getRpcClient() {
		return rpc;
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
