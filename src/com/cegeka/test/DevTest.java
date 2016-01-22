package com.cegeka.test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.cegeka.blocklinks.api.BlocklinksCallable;
import com.cegeka.blocklinks.api.BlocklinksResponse;
import com.cegeka.blocklinks.api.EthereumService;
import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthTransaction;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.Util;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;

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

		File keystore = new File("/home/andreicg/.ethereum/keystore");
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
		EthereumService service = new EthereumService(exec, keystore);

		EthWallet wallet = EthWallet.loadWalletFromString("{\"address\":\"3b4277a7d0314fb70a2afab8c1f94bc20375f33f\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"d5999ea5d1d81fa0c3218a7b02b93db18c7394ca6ecab4e96a9bee4c82573db9\",\"cipherparams\":{\"iv\":\"fb31d04c24a3dee4c31db9271d65f6c4\"},\"kdf\":\"pbkdf2\",\"kdfparams\":{\"prf\":\"hmac-sha256\",\"c\":262144,\"salt\":\"22896ce41107899bf960547affd091ee3101bd242219956ba4331e574faffeea\",\"dklen\":32},\"mac\":\"2bb16bb444159e743b3fde61bf09d9ba8f10c38c1fa15f934fa2046ad2f67c29\"},\"id\":\"ad94ca92-1dbe-457d-ba10-9aeadbd96e26\",\"version\":3}");
		
		wallet.unlock("secret");
		String to = "0x5cc3a427f9c91781625ea36fa3b2f71baa8467bb";
		BigInteger wei = Util.fromEtherToWei(BigDecimal.valueOf(1.2));
		
		EthTransaction tx = new EthTransaction(to, wei);
		service.sendTransaction(wallet, tx, new BlocklinksCallable<String> () {

			@Override
			public void call(BlocklinksResponse<String> response) {
				if (response.getErrType() == null) {
					String txHash = response.getResp();
					System.out.println("Sent transaction " + txHash);
					
					service.listenForTxReceipt(txHash, new BlocklinksCallable<TransactionReceipt>() {

						@Override
						public void call(BlocklinksResponse<TransactionReceipt> response) {
							if (response.getErrType() != null) {
								System.out.println("Error waiting for tx receipt " + response.getEx().getMessage() + " error " + response.getErrType().name());
							} else {
								System.out.println("Tx mined, receipt: " + response.getResp().toString());
							}
						}
					});
				} else {
					System.out.println("Failed to send, error " + response.getErrType().name() + " ex " + response.getEx().getMessage()); 
				}
			}
			
		});
		
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
