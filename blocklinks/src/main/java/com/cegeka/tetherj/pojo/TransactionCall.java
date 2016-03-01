package com.cegeka.tetherj.pojo;

import org.ethereum.core.CallTransaction.Function;

import com.cegeka.tetherj.crypto.CryptoUtil;

public class TransactionCall {
	private Function methodFunction;

	public TransactionCall(Function methodFunction) {
		this.methodFunction = methodFunction;
	}

	public Object[] decodeOutput(String output) {
		return methodFunction.decodeResult(CryptoUtil.hexToBytes(output));
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String from;
	public String to;
	public String gas;
	public String gasPrice;
	public String value;
	public String data;

}
