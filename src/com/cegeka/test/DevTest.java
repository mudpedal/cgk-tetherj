package com.cegeka.test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.ethereum.core.CallTransaction;
import org.ethereum.core.CallTransaction.Function;
import org.ethereum.core.Transaction;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.Util;
import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.crypto.WalletStoragePojoV3;

public class DevTest {

	public static EthRpcClient c = new EthRpcClient();

	public static void main(String[] args) {
		/*
		 * c.unlockAccount(c.getCoinbase(), "secret");
		 * //System.out.println(c.getTransaction(
		 * "0x19945f66caa2b830dac6e479114b10bce11637bace3e131c5c69db77dea0e561")
		 * );
		 * 
		 * String[] acc = c.getAccounts();
		 * System.out.println(Arrays.toString(acc));
		 * 
		 * String tx = c.sendTransaction(acc[0], acc[1],
		 * Util.fromEtherToWei(BigDecimal.ONE));
		 * 
		 * if (tx != null) { System.out.println("Sent transaction " + tx); }
		 * 
		 * System.out.println(c.getTransaction(tx));
		 * 
		 * System.out.println(c.getTransactionReceipt(
		 * "0x19945f66caa2b830dac6e479114b10bce11637bace3e131c5c69db77dea0e561")
		 * );
		 */

		/*
		 * EthWallet wallet = EthWallet.createWallet("secret");
		 * System.out.println("Created wallet " +
		 * wallet.getStorage().toString()); try { String filename =
		 * wallet.generateStandardFilename(); wallet.writeToFile(new
		 * File("/home/andreicg/.ethereum/keystore/" + filename)); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } if (!wallet.isUnlocked()) {
		 * System.out.println("Wallet is locked!"); }
		 * 
		 * boolean unlockHandle = wallet.unlock("secret"); if (unlockHandle &&
		 * wallet.isUnlocked()) { System.out.println("Wallet is now unlocked!");
		 * }
		 * 
		 * System.out.println("Private key from unlock is " +
		 * wallet.getPrivateKey());
		 */

		EthWallet wallet = EthWallet.createWallet("secret");

		BigInteger amount = Util.fromEtherToWei(1);
		BigInteger nonce = BigInteger.ONE;
		BigInteger gasPrice = BigInteger.valueOf(50000000000L);
		BigInteger gasLimit = BigInteger.valueOf(100000L);

		String to2 = "5cc3a427f9c91781625ea36fa3b2f71baa8467bb";
		Transaction tx = Transaction.create(to2, amount, nonce, gasPrice, gasLimit);
		byte[] encoded = tx.getEncoded();
		byte[] encodedRaw = tx.getEncodedRaw();
		System.out.println("Enc " + CryptoUtil.byteToHex(encoded));
		System.out.println("Raw " + CryptoUtil.byteToHex(encodedRaw));

		wallet.unlock("secret");
		tx.sign(CryptoUtil.hexToBytes(wallet.getPrivateKey()));

		encoded = tx.getEncoded();
		encodedRaw = tx.getEncodedRaw();
		System.out.println("Enc " + CryptoUtil.byteToHex(encoded));
		System.out.println("Raw " + CryptoUtil.byteToHex(encodedRaw));

		System.out.println("wallet is " + wallet.getStorage().getAddress());
		System.out.println("PrivKey is " + wallet.getPrivateKey());
		try {
			String filename = wallet.generateStandardFilename();
			wallet.writeToFile(new File("/home/andreicg/.ethereum/keystore/" + filename));
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
