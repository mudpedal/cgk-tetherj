package com.cegeka.blocklinks.api;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;

/**
 * Implementation for a provisioning service for Ethereum
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

	/**
	 * Creates an Ethereum provisioning service
	 * 
	 * @param rootAccount
	 *            account used for funding
	 * @param secret
	 *            secret to be used for operations
	 * @param executor
	 */
	public EthereumService(ScheduledExecutorService executor, File keystoreDir) {
		this.executor = executor;
	}
	
	public void createWallet(String passphrase) {
		throw new UnsupportedOperationException("Not implemented yet!");
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
