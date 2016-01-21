package com.cegeka.blocklinks.ethereum;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cegeka.blocklinks.api.EthereumService;
import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.crypto.WalletStoragePojoV3;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class EthWallet {
	
	WalletStoragePojoV3 storage;
	private byte[] privateKey;
	
	private static final Logger logger = LogManager.getLogger(EthWallet.class);
	
	private EthWallet(WalletStoragePojoV3 storage) {
		privateKey = null;
		this.storage = storage;
	}
	
	public static EthWallet createWallet(String passphrase) {
		EthWallet wallet = new EthWallet(WalletStoragePojoV3.createWallet(passphrase)); 
		logger.info("Generated wallet " + wallet.getStorage().toString());
		return wallet;
	}
	
	/* 
	 * Accepts only version 3 storage wallets
	 */
	public static EthWallet loadWalletFromString(String json) {
		EthWallet wallet = new EthWallet(WalletStoragePojoV3.loadWalletFromString(json));
		logger.debug("Load wallet from string " + wallet.getStorage().toString());
		return wallet;
	}
	
	/* 
	 * Accepts only version 3 storage wallets
	 */
	public static EthWallet loadWalletFromFile(File file) throws IOException {
		EthWallet wallet = new EthWallet(WalletStoragePojoV3.loadWalletFromFile(file));
		logger.debug("Load wallet from string " + wallet.getStorage().toString());
		return wallet;
	}
	
	/* 
	 * Writes the wallet to disk in version 3 format.
	 */
	public void writeToFile(File file) throws IOException {
		logger.debug("Write wallet to file " + file.getAbsolutePath() + " " + storage.toString());
		storage.writeToFile(file);
	}
	
	/* 
	 * Call this to write to geth dir so that geth will allow transactions on this wallet (private key won't
	 * be stored.
	 */
	public void writeToDummyFile(File file) throws IOException {
		logger.debug("Write dummy wallet to file " + file.getAbsolutePath() + " " + storage.toString());
		storage.writeToDummyFile(file);
	}
	
	/* 
	 * Is the private key available? If true then this wallet can be used to sign transactions
	 */
	public boolean isUnlocked() {
		return privateKey != null;
	}
	
	/* 
	 * Uncrypt and store private key(temporarily). May be locked to prevent further signing.
	 * @return Returns true if passphrase was correct.
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
	
	/*
	 * Returns hex privateKey, be careful with it, don't expose it.
	 */
	public String getPrivateKey() {
		if (privateKey != null) {
			return CryptoUtil.byteToHex(privateKey);
		}
		
		return null;
	}
	
	/*
	 * Delete private key from temporary storage, further signing won't be possible.
	 */
	public void lock() {
		logger.debug("Locked wallet " + storage.toString());
		storage = null;
	}

	/*
	 * Get the wallet storage, may be used to get storage information
	 */
	public WalletStoragePojoV3 getStorage() {
		return storage;
	}

	public String generateStandardFilename() {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-DD'T'HH-mm-ss'.'SS");
		DateTime now = DateTime.now(DateTimeZone.UTC);
		
		String filename = "UTC--" + now.toString(fmt) + "--" + storage.getAddress();
		return filename;
	}
}
