package com.cegeka.blocklinks.ethereum;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.crypto.WalletStoragePojoV3;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Wallet container.
 * 
 * @author Andrei Grigoriu
 *
 */
public class EthWallet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4893684742909372607L;
	WalletStoragePojoV3 storage;
	private byte[] privateKey;

	private static final Logger logger = LogManager.getLogger(EthWallet.class);

	/**
	 * Create wallet from storage pojo
	 * 
	 * @param v3
	 *            storage pojo
	 */
	private EthWallet(WalletStoragePojoV3 storage) {
		privateKey = null;
		this.storage = storage;
	}

	/**
	 * Generate a random key pair wallet
	 * 
	 * @param passphrase
	 *            to encrypt private key with
	 * @return the new wallet
	 */
	public static EthWallet createWallet(String passphrase) {
		EthWallet wallet = new EthWallet(WalletStoragePojoV3.createWallet(passphrase));
		logger.info("Generated wallet " + wallet.getStorage().toString());
		return wallet;
	}

	/**
	 * Load wallet from v3 storage json
	 * 
	 * @param json
	 *            in v3 format
	 * @return the wallet
	 */
	public static EthWallet loadWalletFromString(String json) {
		EthWallet wallet = new EthWallet(WalletStoragePojoV3.loadWalletFromString(json));
		logger.debug("Load wallet from string " + wallet.getStorage().toString());
		return wallet;
	}

	/**
	 * Load wallet from file containing v3 json
	 * 
	 * @param file
	 *            to load from
	 * @return the wallet
	 * @throws IOException
	 */
	public static EthWallet loadWalletFromFile(File file) throws IOException {
		EthWallet wallet = new EthWallet(WalletStoragePojoV3.loadWalletFromFile(file));
		logger.debug("Load wallet from string " + wallet.getStorage().toString());
		return wallet;
	}

	/**
	 * Write v3 storage to disk
	 * 
	 * @param file
	 *            to write to
	 * @throws IOException
	 */
	public void writeToFile(File file) throws IOException {
		logger.debug("Write wallet to file " + file.getAbsolutePath() + " " + storage.toString());
		storage.writeToFile(file);
	}

	/**
	 * Is private key available in memory
	 * 
	 * @return true if private key is decrypted
	 */
	public boolean isUnlocked() {
		return privateKey != null;
	}

	/**
	 * Decrypt private key and store it in memory
	 * 
	 * @param passphrase
	 *            to decrypt
	 * @return true if succeeded
	 */
	public boolean unlock(String passphrase) {
		privateKey = storage.getPrivateKey(passphrase);
		if (privateKey == null) {
			logger.debug("Failed to unlock wallet " + storage.toString());
			return false;
		}

		logger.debug("Unlocked wallet " + storage.toString());
		return true;
	}

	/**
	 * Works if unlocked, otherwise returns null
	 * 
	 * @return null if locked, hex private key otherwise
	 */
	public String getPrivateKey() {
		if (privateKey != null) {
			return CryptoUtil.byteToHex(privateKey);
		}

		return null;
	}

	/**
	 * delete private key from memory
	 */
	public void lock() {
		logger.debug("Locked wallet " + storage.toString());
		storage = null;
	}

	/**
	 * 
	 * @return the complete storage v3
	 */
	public WalletStoragePojoV3 getStorage() {
		return storage;
	}
	
	/**
	 * Set storage
	 * @param storage
	 */
	public void setStorage(WalletStoragePojoV3 storage) {
		this.storage = storage;
	}

	/**
	 * @return wallet address
	 */
	public String getAddress() {
		if (storage != null) {
			return storage.getAddress();
		}

		return null;
	}

	/**
	 * Generate ethereum client standard filename (uses now date)
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
