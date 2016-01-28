package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;

import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.pojo.Block;
import com.cegeka.blocklinks.ethereum.pojo.Contracts;
import com.cegeka.blocklinks.ethereum.pojo.Transaction;
import com.cegeka.blocklinks.ethereum.pojo.TransactionCall;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;
import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class EthRpcClient {
	private EthRpcInterface rpc;
	public final static String defaultHostname = "localhost";
	public final static int defaultPort = 8545;

	public EthRpcClient() {
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

	public String getCoinbase() throws JsonRpcClientException {
		return rpc.eth_coinbase();
	}

	public String[] getAccounts() throws JsonRpcClientException {
		return rpc.eth_accounts();
	}

	public BigInteger getAccountNonce(String address) throws JsonRpcClientException {
		String txCount = rpc.eth_getTransactionCount(address, "latest");
		return CryptoUtil.hexToBigInteger(txCount);
	}

	public boolean unlockAccount(String address, String secret) throws JsonRpcClientException {
		return rpc.personal_unlockAccount(address, secret);
	}

	public String sendTransaction(String from, String fromSecret, String to, BigInteger valueWei)
			throws JsonRpcClientException {
		boolean unlock = rpc.personal_unlockAccount(from, fromSecret);

		if (unlock) {
			return this.sendTransaction(from, to, valueWei);
		}
		return null;
	}

	public String sendTransaction(String from, String to, BigInteger valueWei) throws JsonRpcClientException {
		Transaction t = new Transaction();

		t.setFrom(from.toString());
		t.setTo(to);
		t.setValue(valueWei.toString());

		return rpc.eth_sendTransaction(t);
	}

	public String sendRawTransaction(String encodedSignedTransaction) throws JsonRpcClientException {
		return rpc.eth_sendRawTransaction(encodedSignedTransaction);
	}

	public String sendRawTransaction(byte[] encodedSignedTransaction) throws JsonRpcClientException {
		return sendRawTransaction(CryptoUtil.byteToHex(encodedSignedTransaction));
	}

	public BigInteger getBalance(String address) throws JsonRpcClientException {
		String balance = rpc.eth_getBalance(address);
		return CryptoUtil.hexToBigInteger(balance);
	}

	public TransactionReceipt getTransactionReceipt(String txHash) throws JsonRpcClientException {
		return rpc.eth_getTransactionReceipt(txHash);
	}

	public Transaction getTransaction(String txHash) throws JsonRpcClientException {
		return rpc.eth_getTransactionByHash(txHash);
	}

	public String callMethod(TransactionCall call) {
		return rpc.eth_call(call);
	}
	
	public String callMethod(EthCall call) {
		return rpc.eth_call(call.getCall());
	}

	public BigInteger getBlockGasLimit() {
		Block block = rpc.eth_getBlockByNumber("latest", true);
		if (block != null) {
			return CryptoUtil.hexToBigInteger(block.gasLimit);
		}

		return null;
	}

	public Contracts compileSolidity(String sourceCode) {
		return rpc.eth_compileSolidity(sourceCode);
	}
}
