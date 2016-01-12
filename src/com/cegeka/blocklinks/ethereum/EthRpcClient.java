package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;

import com.cegeka.blocklinks.ethereum.pojo.Transaction;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class EthRpcClient {
	private EthRpcInterface rpc;
	public static String defaultHostname = "localhost";
	public static int defaultPort = 8545;

	public EthRpcClient () {
		this(defaultHostname, defaultPort);
	}
	
	public EthRpcClient(String hostname, int port) {
		URL url;
		try {
			url = new URL("http://" + hostname + ":" + port + "/");
			JsonRpcHttpClient rpcClient = new JsonRpcHttpClient(url);
			rpc = ProxyUtil.createClientProxy(getClass().getClassLoader(), EthRpcInterface.class, rpcClient);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public String getCoinbase() {
		return rpc.eth_coinbase();
	}
	
	public String[] getAccounts() {
		return rpc.eth_accounts();
	}

	public boolean unlockAccount(String address, String secret) {
		return rpc.personal_unlockAccount(address, secret);
	}

	public String sendTransaction(String from, String fromSecret, String to, BigInteger valueWei) {
		boolean unlock = rpc.personal_unlockAccount(from, fromSecret);

		if (unlock) {
			return this.sendTransaction(from, to, valueWei);
		}

		return null;
	}

	public String sendTransaction(String from, String to, BigInteger valueWei) {
		Transaction t = new Transaction();

		t.setFrom(from.toString());
		t.setTo(to);
		t.setValue(valueWei.toString());

		return rpc.eth_sendTransaction(t);
	}

	public BigInteger getBalance(String address) {
		String balance = rpc.eth_getBalance(address);

		// remove 0x
		if (balance.startsWith("0x")) {
			balance = balance.substring(2);
		}

		// remove starting zeros
		while (balance.length() > 0 && balance.charAt(0) == '0') {
			balance = balance.substring(1);
		}

		return new BigInteger(balance, 16);
	}

	public TransactionReceipt getTransactionReceipt(String txHash) {
		return rpc.eth_getTransactionReceipt(txHash);
	}
	
	public Transaction getTransaction(String txHash) {
		return rpc.eth_getTransactionByHash(txHash);
	}
}
