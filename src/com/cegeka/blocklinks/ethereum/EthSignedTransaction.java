package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;

import org.ethereum.crypto.SHA3Helper;

/**
 * Immutable Holds transaction data
 * 
 * @author andreicg
 *
 */
public class EthSignedTransaction {

	private final String hash;
	private final String from;
	private final String to;
	private final BigInteger value;
	private final BigInteger nonce;
	private final byte[] signedEcodedData;

	public EthSignedTransaction(String transactionHash, String from, String to, BigInteger value, BigInteger nonce,
			byte[] signedEcodedData) {

		this.hash = transactionHash;
		this.from = from;
		this.to = to;
		this.value = value;
		this.nonce = nonce;
		this.signedEcodedData = signedEcodedData;
	}

	public EthSignedTransaction(EthTransaction transaction, String from, BigInteger nonce, byte[] signedEcodedData) {
		this.hash = SHA3Helper.sha3String(signedEcodedData);
		this.from = from;
		this.nonce = nonce;
		this.value = transaction.getWeiValue();
		this.to = transaction.getTo();
		this.signedEcodedData = signedEcodedData;
	}

	public String getTransactionHash() {
		return hash;
	}

	public String getFrom() {
		return from;
	}

	public BigInteger getNonce() {
		return nonce;
	}

	public byte[] getSignedEcodedData() {
		return signedEcodedData;
	}

	public String getTo() {
		return to;
	}

	public BigInteger getValue() {
		return value;
	}
}
