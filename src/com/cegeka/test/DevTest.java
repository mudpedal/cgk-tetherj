package com.cegeka.test;

import java.io.File;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.cegeka.blocklinks.api.BlocklinksCallable;
import com.cegeka.blocklinks.api.BlocklinksResponse;
import com.cegeka.blocklinks.api.EthereumService;
import com.cegeka.blocklinks.api.WalletLockedException;
import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthTransaction;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.Util;
import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.crypto.WalletStoragePojoV3;
import com.googlecode.jsonrpc4j.JsonRpcClientException;

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

		/*
		 * EthWallet wallet = EthWallet.createWallet("secret");
		 * 
		 * String to2 = "5cc3a427f9c91781625ea36fa3b2f71baa8467bb"; try { String
		 * filename = wallet.generateStandardFilename();
		 * wallet.writeToDummyFile(new File("/home/andreicg/.ethereum/keystore/"
		 * + filename)); } catch (IOException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 * 
		 * try { c.getAccounts(); EthTransaction tx = new EthTransaction(to2,
		 * BigInteger.ONE); BigInteger nonce =
		 * c.getAccountNonce(wallet.getStorage().getAddress()); byte[] raw =
		 * tx.signWithWallet(wallet, nonce, "secret"); System.out.println(
		 * "Signed: " + CryptoUtil.byteToHex(raw));
		 * 
		 * c.sendRawTransaction(raw); } catch (WalletLockedException e) {
		 * e.printStackTrace(); } catch (JsonRpcClientException je) {
		 * je.printStackTrace(); } catch (UndeclaredThrowableException ce) {
		 * ce.printStackTrace(); }
		 * 
		 */

		File keystore = new File("/home/andreicg/.ethereum/keystore");
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
		EthereumService service = new EthereumService(exec, keystore);

		service.getAccounts(new BlocklinksCallable<String[]>() {

			@Override
			public void call(BlocklinksResponse<String[]> response) {
				if (response.getErrType() != null) {
					System.out.println("GOT ERROR " + response.getErrType().name());
				} else {
					System.out.println("GOT response " + Arrays.toString(response.getResp()));
				}
			}
		});

	}
}
