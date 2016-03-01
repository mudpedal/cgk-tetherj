package com.cegeka.tetherj.pojo;

public class Transaction {

	@Override
	public String toString() {
		return "Transaction [blockHash=" + blockHash + ", blockNumber=" + blockNumber + ", from=" + from + ", gas="
				+ gas + ", gasPrice=" + gasPrice + ", hash=" + hash + ", input=" + input + ", nonce=" + nonce + ", to="
				+ to + ", transactionIndex=" + transactionIndex + ", value=" + value + "]";
	}

	public String blockHash;

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public String getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(String blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getGas() {
		return gas;
	}

	public void setGas(String gas) {
		this.gas = gas;
	}

	public String getGasPrice() {
		return gasPrice;
	}

	public void setGasPrice(String gasPrice) {
		this.gasPrice = gasPrice;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTransactionIndex() {
		return transactionIndex;
	}

	public void setTransactionIndex(String transactionIndex) {
		this.transactionIndex = transactionIndex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String blockNumber;
	public String from;
	public String gas;
	public String gasPrice;
	public String hash;
	public String input;
	public String nonce;
	public String to;
	public String transactionIndex;
	public String value;
}
