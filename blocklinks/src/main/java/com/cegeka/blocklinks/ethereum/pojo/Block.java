package com.cegeka.blocklinks.ethereum.pojo;

public class Block {
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getParentHash() {
		return parentHash;
	}

	public void setParentHash(String parentHash) {
		this.parentHash = parentHash;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getSha3Uncles() {
		return sha3Uncles;
	}

	public void setSha3Uncles(String sha3Uncles) {
		this.sha3Uncles = sha3Uncles;
	}

	public String getLogsBloom() {
		return logsBloom;
	}

	public void setLogsBloom(String logsBloom) {
		this.logsBloom = logsBloom;
	}

	public String getTransactionsRoot() {
		return transactionsRoot;
	}

	public void setTransactionsRoot(String transactionsRoot) {
		this.transactionsRoot = transactionsRoot;
	}

	public String getStateRoot() {
		return stateRoot;
	}

	public void setStateRoot(String stateRoot) {
		this.stateRoot = stateRoot;
	}

	public String getReceiptRoot() {
		return receiptRoot;
	}

	public void setReceiptRoot(String receiptRoot) {
		this.receiptRoot = receiptRoot;
	}

	public String getMiner() {
		return miner;
	}

	public void setMiner(String miner) {
		this.miner = miner;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public String getTotalDifficulty() {
		return totalDifficulty;
	}

	public void setTotalDifficulty(String totalDifficulty) {
		this.totalDifficulty = totalDifficulty;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getGasLimit() {
		return gasLimit;
	}

	public void setGasLimit(String gasLimit) {
		this.gasLimit = gasLimit;
	}

	public String getGasUsed() {
		return gasUsed;
	}

	public void setGasUsed(String gasUsed) {
		this.gasUsed = gasUsed;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String[] getTransactions() {
		return transactions;
	}

	public void setTransactions(String[] transactions) {
		this.transactions = transactions;
	}

	public String[] getUncles() {
		return uncles;
	}

	public void setUncles(String[] uncles) {
		this.uncles = uncles;
	}

	public String number;
	public String hash;
	public String parentHash;
	public String nonce;
	public String sha3Uncles;
	public String logsBloom;
	public String transactionsRoot;
	public String stateRoot;
	public String receiptRoot;
	public String miner;
	public String difficulty;
	public String totalDifficulty;
	public String extraData;
	public String size;
	public String gasLimit;
	public String gasUsed;
	public String timestamp;
	public String[] transactions;
	public String[] uncles;
}
