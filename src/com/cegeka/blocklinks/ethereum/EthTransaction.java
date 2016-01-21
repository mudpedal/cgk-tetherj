package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;

import org.ethereum.core.Transaction;

import com.cegeka.blocklinks.api.WalletLockedException;
import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;

public class EthTransaction {
	BigInteger weiValue;
	BigInteger gasPrice = BigInteger.valueOf(50000000000L);
	String to;
	BigInteger gasLimit = BigInteger.valueOf(90000L);
	
	public EthTransaction(String to, BigInteger weiValue) {
		this.to = to;
		this.weiValue = weiValue; 
	}
	
	public byte[] signWithWallet(EthWallet wallet, BigInteger nonce) throws WalletLockedException {
		String privateKey = wallet.getPrivateKey();
		
		if (privateKey == null) {
			throw new WalletLockedException();
		}
		
		if (to.startsWith("0x")) {
			to = to.substring(2);
		}
		
		Transaction tx = Transaction.create(to, weiValue, nonce, gasPrice, gasLimit);
		tx.sign(CryptoUtil.hexToBytes(privateKey));
		return tx.getEncoded();
	}
	
	public byte[] signWithWallet(EthWallet wallet, BigInteger nonce, String passphrase) throws WalletLockedException {
		if (!wallet.isUnlocked()) {
			wallet.unlock(passphrase);
		}
		
		return signWithWallet(wallet, nonce);
	}
}
