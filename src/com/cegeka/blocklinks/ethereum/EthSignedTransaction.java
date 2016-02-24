package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;
import java.util.Arrays;

import org.ethereum.crypto.HashUtil;

import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;

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
		this.hash = CryptoUtil.byteToHexWithPrefix(HashUtil.sha3(signedEcodedData));
		this.from = from;
		this.nonce = nonce;
		this.value = transaction.getWeiValue();
		this.to = transaction.getTo();
		this.signedEcodedData = signedEcodedData;
	}

	public String getHash() {
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((nonce == null) ? 0 : nonce.hashCode());
		result = prime * result + Arrays.hashCode(signedEcodedData);
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EthSignedTransaction other = (EthSignedTransaction) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (nonce == null) {
			if (other.nonce != null)
				return false;
		} else if (!nonce.equals(other.nonce))
			return false;
		if (!Arrays.equals(signedEcodedData, other.signedEcodedData))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
