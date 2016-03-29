package com.cegeka.tetherj;

import java.math.BigInteger;
import java.util.Arrays;

import org.ethereum.core.Transaction;

import com.cegeka.tetherj.api.WalletLockedException;
import com.cegeka.tetherj.crypto.CryptoUtil;

/**
 * 
 * @author Andrei Grigoriu Container for data for a future transaction.
 *
 */
public class EthTransaction {
    public static final BigInteger defaultGasPrice = BigInteger.valueOf(50000000000L);
    public static final BigInteger defaultGasLimit = BigInteger.valueOf(90000L);

    /* may change in future ethereum updates */
    public static final BigInteger maximumGasLimit = BigInteger.valueOf(3141592L);

    @Override
    public String toString() {
        return "EthTransaction [to=" + to + ", gasPrice=" + gasPrice + ", gasLimit=" + gasLimit
                + ", data=" + Arrays.toString(data) + ", weiValue=" + weiValue + "]";
    }

    String to;
    BigInteger gasPrice = defaultGasPrice;
    BigInteger gasLimit = defaultGasLimit;
    byte[] data = null;
    BigInteger weiValue;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigInteger getWeiValue() {
        return weiValue;
    }

    public void setWeiValue(BigInteger weiValue) {
        this.weiValue = weiValue;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public static BigInteger getDefaultgasprice() {
        return defaultGasPrice;
    }

    public static BigInteger getMaximumGasLimit() {
        return maximumGasLimit;
    }

    public static BigInteger getDefaultgaslimit() {
        return defaultGasLimit;
    }

    /**
     * Default empty constructor
     */
    public EthTransaction() {
    }

    /**
     * 
     * @param to
     *            receiver (may be contract)
     * @param weiValue
     *            wei to send
     */
    public EthTransaction(String to, BigInteger weiValue) {
        this.to = to;
        this.weiValue = weiValue;
    }

    /**
     * 
     * @param to
     *            receiver (may be contract)
     * @param weiValue
     *            wei to send
     * @param gasPrice
     *            gas price
     * @param gasLimit
     *            gas limit
     * @param data
     *            any transaction data
     */
    public EthTransaction(String to, BigInteger weiValue, BigInteger gasPrice, BigInteger gasLimit,
            byte[] data) {
        this.to = to;
        this.weiValue = weiValue;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.data = data;
    }

    /**
     * Sign and return encoded transaction data
     * 
     * @param wallet
     *            to sign with (has to be unlocked)
     * @param nonce
     *            to sign with
     * @return encoded transaction data
     * @throws WalletLockedException
     */
    public EthSignedTransaction signWithWallet(EthWallet wallet, BigInteger nonce)
            throws WalletLockedException {
        String privateKey = wallet.getPrivateKey();

        if (privateKey == null) {
            throw new WalletLockedException();
        }

        if (to == null) {
            to = "";
        } else if (to.startsWith("0x")) {
            to = to.substring(2);
        }

        Transaction tx = Transaction.create(to, weiValue, nonce, gasPrice, gasLimit, data);
        tx.sign(CryptoUtil.hexToBytes(privateKey));

        return new EthSignedTransaction(this, wallet.getAddress(), nonce, tx.getEncoded());
    }

    /**
     * Sign and return encoded transaction data
     * 
     * @param wallet
     *            to sign with
     * @param nonce
     *            to sign with
     * @param passphrase
     *            to unlock wallet with in case its locked
     * @return encoded transaction data
     * @throws WalletLockedException
     */
    public EthSignedTransaction signWithWallet(EthWallet wallet, BigInteger nonce,
            String passphrase) throws WalletLockedException {
        if (!wallet.isUnlocked()) {
            wallet.unlock(passphrase);
        }

        return signWithWallet(wallet, nonce);
    }
}
