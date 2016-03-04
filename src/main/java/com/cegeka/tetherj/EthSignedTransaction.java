package com.cegeka.tetherj;

import java.math.BigInteger;
import java.util.Arrays;

import org.ethereum.crypto.HashUtil;

import com.cegeka.tetherj.crypto.CryptoUtil;

/**
 * Holds transaction data
 * 
 * @author andreicg
 *
 */
public class EthSignedTransaction implements Comparable<EthSignedTransaction> {

	private String hash;
	private String from;
	private String to;
	private BigInteger value;
	private BigInteger nonce;
	private byte[] signedEcodedData;

	public EthSignedTransaction() {
	}

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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
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

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}

	public BigInteger getNonce() {
		return nonce;
	}

	public void setNonce(BigInteger nonce) {
		this.nonce = nonce;
	}

	public byte[] getSignedEncodedData() {
		return signedEcodedData;
	}

	public void setSignedEcodedData(byte[] signedEcodedData) {
		this.signedEcodedData = signedEcodedData;
	}

	/**
	 * Compares by nonce. Compare only signed transactions from the same wallet
	 * to actually make sense.
	 */
	@Override
	public int compareTo(EthSignedTransaction o) {
		return this.nonce.compareTo(o.getNonce());
	}

}
