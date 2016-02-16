package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;

public class EthSignedTransaction {

	private final EthTransaction transaction;
	private final String addressFrom;
	private final BigInteger nonce;
	private final byte[] signature;
	
	public EthSignedTransaction(EthTransaction transaction, String addressFrom, BigInteger nonce, byte[] signature) {
		this.transaction = transaction;
		this.addressFrom = addressFrom;
		this.nonce = nonce;
		this.signature = signature;
	}
	
	public EthTransaction getTransaction() {
		return transaction;
	}

	public String getAddressFrom() {
		return addressFrom;
	}

	public BigInteger getNonce() {
		return nonce;
	}

	public byte[] getSignature() {
		return signature;
	}
}
