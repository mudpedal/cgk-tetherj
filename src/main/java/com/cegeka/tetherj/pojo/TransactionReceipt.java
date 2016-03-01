package com.cegeka.tetherj.pojo;

import java.util.Arrays;

public class TransactionReceipt {

	@Override
	public String toString() {
		return "TransactionReceipt [blockHash=" + blockHash + ", blockNumber=" + blockNumber + ", contractAddress="
				+ contractAddress + ", cumulativeGasUsed=" + cumulativeGasUsed + ", gasUsed=" + gasUsed + ", logs="
				+ Arrays.toString(logs) + ", transactionHash=" + transactionHash + ", transactionIndex="
				+ transactionIndex + "]";
	}

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

	public String getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}

	public String getCumulativeGasUsed() {
		return cumulativeGasUsed;
	}

	public void setCumulativeGasUsed(String cumulativeGasUsed) {
		this.cumulativeGasUsed = cumulativeGasUsed;
	}

	public String getGasUsed() {
		return gasUsed;
	}

	public void setGasUsed(String gasUsed) {
		this.gasUsed = gasUsed;
	}

	public Object[] getLogs() {
		return logs;
	}

	public void setLogs(Object[] logs) {
		this.logs = logs;
	}

	public String getTransactionHash() {
		return transactionHash;
	}

	public void setTransactionHash(String transactionHash) {
		this.transactionHash = transactionHash;
	}

	public String getTransactionIndex() {
		return transactionIndex;
	}

	public void setTransactionIndex(String transactionIndex) {
		this.transactionIndex = transactionIndex;
	}

	private String blockHash;
	private String blockNumber;
	private String contractAddress;
	private String cumulativeGasUsed;
	private String gasUsed;
	private Object[] logs;
	private String transactionHash;
	private String transactionIndex;
}
