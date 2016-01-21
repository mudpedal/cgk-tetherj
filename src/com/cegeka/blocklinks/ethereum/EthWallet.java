package com.cegeka.blocklinks.ethereum;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.crypto.WalletStoragePojoV3;

public class EthWallet {
	WalletStoragePojoV3 storage;
	private byte[] privateKey;
	
	private EthWallet(WalletStoragePojoV3 storage) {
		privateKey = null;
		this.storage = storage;
	}
	
	public static EthWallet createWallet(String passphrase) {
		return new EthWallet(WalletStoragePojoV3.createWallet(passphrase));
	}
	
	/* 
	 * Accepts only version 3 storage wallets
	 */
	public static EthWallet loadWalletFromString(String json) {
		return new EthWallet(WalletStoragePojoV3.loadWalletFromString(json));
	}
	
	/* 
	 * Accepts only version 3 storage wallets
	 */
	public static EthWallet loadWalletFromFile(File file) throws IOException {
		return new EthWallet(WalletStoragePojoV3.loadWalletFromFile(file));
	}
	
	/* 
	 * Writes the wallet to disk in version 3 format.
	 */
	public void writeToFile(File file) throws IOException {
		storage.writeToFile(file);
	}
	
	/* 
	 * Call this to write to geth dir so that geth will allow transactions on this wallet (private key won't
	 * be stored.
	 */
	public void writeToDummyFile(File file) throws IOException {
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
			return false;
		}
		
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
