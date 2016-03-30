package com.cegeka.tetherj;

import java.math.BigInteger;

import org.ethereum.crypto.HashUtil;

import com.cegeka.tetherj.crypto.CryptoUtil;

import lombok.Data;

/**
 * Holds signed transaction data.
 * 
 * @author Andrei Grigoriu
 *
 */
@Data
public class EthSignedTransaction implements Comparable<EthSignedTransaction> {

    private String hash;
    private String from;
    private String to;
    private BigInteger value;
    private BigInteger nonce;
    private byte[] signedEncodedData;

    public EthSignedTransaction() {
    }

    /**
     * Constructor with all data.
     * 
     * @param transactionHash
     *            Hash of the transaction.
     * @param from
     *            From address field of the transaction.
     * @param to
     *            To address field of the transaction.
     * @param value
     *            Value in wei field of the transaction.
     * @param nonce
     *            Nonce value of the transaction.
     * @param signedEncodedData
     *            The entire signed blob of the transaction.
     */
    public EthSignedTransaction(String transactionHash, String from, String to, BigInteger value,
            BigInteger nonce, byte[] signedEncodedData) {

        this.hash = transactionHash;
        this.from = from;
        this.to = to;
        this.value = value;
        this.nonce = nonce;
        this.signedEncodedData = signedEncodedData;
    }

    /**
     * Construct signed transaction from a transaction.
     * 
     * @param transaction
     *            Unsigned Transaction object.
     * @param from
     *            From address field of the transaction.
     * @param nonce
     *            Nonce value of the transaction.
     * @param signedEncodedData
     *            The entire signed blob of the transaction.
     */
    public EthSignedTransaction(EthTransaction transaction, String from, BigInteger nonce,
            byte[] signedEncodedData) {
        this.hash = CryptoUtil.byteToHexWithPrefix(HashUtil.sha3(signedEncodedData));
        this.from = from;
        this.nonce = nonce;
        this.value = transaction.getWeiValue();
        this.to = transaction.getTo();
        this.signedEncodedData = signedEncodedData;
    }

    /**
     * Compares by nonce. Compare only signed transactions from the same wallet to actually make
     * sense.
     */
    @Override
    public int compareTo(EthSignedTransaction other) {
        return this.nonce.compareTo(other.getNonce());
    }

}
