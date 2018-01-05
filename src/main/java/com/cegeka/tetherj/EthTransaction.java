package com.cegeka.tetherj;

import java.math.BigInteger;
import java.util.Arrays;

import org.ethereum.core.Transaction;

import com.cegeka.tetherj.api.WalletLockedException;
import com.cegeka.tetherj.crypto.CryptoUtil;

/**
 * Container for data for a future transaction.
 * 
 * @author Andrei Grigoriu
 *
 */
public class EthTransaction {
    public static final BigInteger DEFAULT_GAS_PRICE = BigInteger.valueOf(50000000000L);
    public static final BigInteger DEFAULT_GAS_LIMIT = BigInteger.valueOf(90000L);

    /* may change in future ethereum updates */
    public static final BigInteger maximumGasLimit = BigInteger.valueOf(3141592L);

    @Override
    public String toString() {
        return "EthTransaction [to=" + to + ", gasPrice=" + gasPrice + ", gasLimit=" + gasLimit
                + ", data=" + Arrays.toString(data) + ", weiValue=" + weiValue + "]";
    }

    String to;
    BigInteger gasPrice = DEFAULT_GAS_PRICE;
    BigInteger gasLimit = DEFAULT_GAS_LIMIT;
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
        return DEFAULT_GAS_PRICE;
    }

    public static BigInteger getMaximumGasLimit() {
        return maximumGasLimit;
    }

    public static BigInteger getDefaultgaslimit() {
        return DEFAULT_GAS_LIMIT;
    }

    /**
     * Noarg constructor.
     */
    public EthTransaction() {
    }

    /**
     * Construct simple transaction.
     * 
     * @param to
     *            Address to send to.
     * @param weiValue
     *            Amount in wei to send.
     */
    public EthTransaction(String to, BigInteger weiValue) {
        this.to = to;
        this.weiValue = weiValue;
    }

    /**
     * Construct complex transaction.
     * 
     * @param to
     *            Address to send to.
     * @param weiValue
     *            Amount in wei to send.
     * @param gasPrice
     *            Gas price to set.
     * @param gasLimit
     *            Gas limit to set.
     * @param data
     *            Transaction data to set.
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
     * Sign a transaction.
     * 
     * @param wallet
     *            Wallet to sign with, must be unlocked!
     * @param nonce
     *            Nonce to sign transaction with.
     * @return Returns signed transaction.
     * @throws WalletLockedException
     *             If the wallet is locked.
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

        Transaction tx = Transaction.create(to, weiValue, nonce, gasPrice, gasLimit, data, 1);
        tx.sign(CryptoUtil.hexToBytes(privateKey));

        return new EthSignedTransaction(this, wallet.getAddress(), nonce, tx.getEncoded());
    }

    /**
     * Sign a transaction.
     * 
     * @param wallet
     *            Wallet to sign with.
     * @param nonce
     *            Nonce to sign transaction with.
     * @param passphrase Passphrase to unlock wallet with.
     * @return Returns signed transaction.
     * @throws WalletLockedException
     *             If the wallet is locked, the passphrase was bad.
     */
    public EthSignedTransaction signWithWallet(EthWallet wallet, BigInteger nonce,
            String passphrase) throws WalletLockedException {
        if (!wallet.isUnlocked()) {
            wallet.unlock(passphrase);
        }

        return signWithWallet(wallet, nonce);
    }
}
