package com.cegeka.tetherj;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.crypto.WalletStoragePojoV3;

import lombok.Getter;
import lombok.Setter;

/**
 * Wallet container.
 * 
 * @author Andrei Grigoriu
 *
 */
public class EthWallet implements Serializable {

    private static final long serialVersionUID = -4893684742909372607L;

    @Getter
    @Setter
    WalletStoragePojoV3 storage;
    private byte[] privateKey;

    private static final Logger logger = LogManager.getLogger(EthWallet.class);

    public EthWallet() {
        privateKey = null;
    }

    /**
     * Create wallet from storage pojo.
     * 
     * @param storage
     *            The storage object.
     */
    public EthWallet(WalletStoragePojoV3 storage) {
        privateKey = null;
        this.storage = storage;
    }

    public EthWallet(String privateKey) {
        this.privateKey = CryptoUtil.hexToBytes(privateKey);
        this.storage = WalletStoragePojoV3.createFromPrivateKey(this.privateKey);
    }

    /**
     * Generate a random key pair wallet.
     * 
     * @param passphrase
     *            Pass to encrypt private key with.
     * @return Returns the new wallet.
     */
    public static EthWallet createWallet(String passphrase) {
        EthWallet wallet = new EthWallet(WalletStoragePojoV3.createWallet(passphrase));
        logger.info("Generated wallet " + wallet.getStorage().toString());
        return wallet;
    }

    /**
     * Load wallet from v3 storage json.
     * 
     * @param json
     *            Json string in v3 format.
     * @return Returns the wallet.
     */
    public static EthWallet loadWalletFromString(String json) {
        EthWallet wallet = new EthWallet(WalletStoragePojoV3.loadWalletFromString(json));
        logger.debug("Load wallet from string " + wallet.getStorage().toString());
        return wallet;
    }

    /**
     * Load wallet from file containing v3 json.
     * 
     * @param file
     *            to load from, containing json string in v3 format.
     * @return Returns the wallet.
     * @throws IOException
     *             In case of IO errors.
     */
    public static EthWallet loadWalletFromFile(File file) throws IOException {
        EthWallet wallet = new EthWallet(WalletStoragePojoV3.loadWalletFromFile(file));
        logger.debug("Load wallet from string " + wallet.getStorage().toString());
        return wallet;
    }

    /**
     * Write v3 storage to disk.
     * 
     * @param file
     *            File to write to.
     * @throws IOException
     *             In case of IO errors.
     */
    public void writeToFile(File file) throws IOException {
        logger.debug("Write wallet to file " + file.getAbsolutePath() + " " + storage.toString());
        storage.writeToFile(file);
    }

    /**
     * Is private key available in memory.
     * 
     * @return Returns true if private key is decrypted.
     */
    public boolean isUnlocked() {
        return privateKey != null;
    }

    /**
     * Decrypt private key and store it in memory.
     * 
     * @param passphrase
     *            Passphrase to decrypt with.
     * @return Returns true if succeeded.
     */
    public boolean unlock(String passphrase) {
        if (storage.getCrypto() != null) {
            privateKey = storage.getPrivateKey(passphrase);
            if (privateKey == null) {
                logger.debug("Failed to unlock wallet " + storage.toString());
                return false;
            }

            logger.debug("Unlocked wallet " + storage.toString());
        }

        if (privateKey == null) {
            return false;
        }

        return true;
    }

    /**
     * Returns private key if the wallet is unlocked.
     * 
     * @return null if locked, hex private key otherwise.
     */
    public String getPrivateKey() {
        if (privateKey != null) {
            return CryptoUtil.byteToHex(privateKey);
        }

        return null;
    }

    /**
     * Deletes private key from memory.
     */
    public void lock() {
        logger.debug("Locked wallet " + storage.toString());
        storage = null;
    }

    /**
     * @return Returns wallet address.
     */
    public String getAddress() {
        if (storage != null) {
            return storage.getAddress();
        }

        return null;
    }

    /**
     * Generate ethereum client standard filename (uses current time).
     * 
     * @return file name
     */
    public String generateStandardFilename() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-DD'T'HH-mm-ss'.'SS");
        DateTime now = DateTime.now(DateTimeZone.UTC);

        String filename = "UTC--" + now.toString(fmt) + "--" + storage.getAddress();
        return filename;
    }
}
