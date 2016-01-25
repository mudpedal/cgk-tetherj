package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;

import org.ethereum.core.Transaction;

import com.cegeka.blocklinks.api.WalletLockedException;
import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;

public class EthTransaction {
	public static final BigInteger defaultGasPrice = BigInteger.valueOf(50000000000L);
	public static final BigInteger defaultGasLimit = BigInteger.valueOf(90000L);
	BigInteger weiValue;
	BigInteger gasPrice = defaultGasPrice;
	String to;
	BigInteger gasLimit = defaultGasLimit;
	byte[] data = null;
	
	public EthTransaction(String to, BigInteger weiValue) {
		this.to = to;
		this.weiValue = weiValue; 
	}
	
	public EthTransaction(String to, BigInteger weiValue, BigInteger gasPrice, BigInteger gasLimit, byte[] data) {
		this.to = to;
		this.weiValue = weiValue;
		this.gasPrice = gasPrice;
		this.gasLimit = gasLimit;
		this.data = data;
	}
	
	public byte[] signWithWallet(EthWallet wallet, BigInteger nonce) throws WalletLockedException {
		System.out.println(nonce);
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
		return tx.getEncoded();
	}
	
	public byte[] signWithWallet(EthWallet wallet, BigInteger nonce, String passphrase) throws WalletLockedException {
		if (!wallet.isUnlocked()) {
			wallet.unlock(passphrase);
		}
		
		return signWithWallet(wallet, nonce);
	}
}
