package com.cegeka.tetherj;

import java.math.BigInteger;

import org.ethereum.crypto.HashUtil;

import com.cegeka.tetherj.crypto.CryptoUtil;

import lombok.Data;

/**
 * Holds transaction data
 * 
 * @author andreicg
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

    public EthSignedTransaction(String transactionHash, String from, String to, BigInteger value,
            BigInteger nonce, byte[] signedEcodedData) {

        this.hash = transactionHash;
        this.from = from;
        this.to = to;
        this.value = value;
        this.nonce = nonce;
        this.signedEncodedData = signedEcodedData;
    }

    public EthSignedTransaction(EthTransaction transaction, String from, BigInteger nonce,
            byte[] signedEcodedData) {
        this.hash = CryptoUtil.byteToHexWithPrefix(HashUtil.sha3(signedEcodedData));
        this.from = from;
        this.nonce = nonce;
        this.value = transaction.getWeiValue();
        this.to = transaction.getTo();
        this.signedEncodedData = signedEcodedData;
    }

    /**
     * Compares by nonce. Compare only signed transactions from the same wallet to actually make
     * sense.
     */
    @Override
    public int compareTo(EthSignedTransaction o) {
        return this.nonce.compareTo(o.getNonce());
    }

}
