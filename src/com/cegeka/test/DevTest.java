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
import com.cegeka.blocklinks.api.WalletLockedException;
import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthSmartContract;
import com.cegeka.blocklinks.ethereum.EthSmartContractFactory;
import com.cegeka.blocklinks.ethereum.EthTransaction;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.NoSuchContractMethod;
import com.cegeka.blocklinks.ethereum.Util;
import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.pojo.ContractInfo;
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

		/*
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
		*/
		
		File keystore = new File("/home/andreicg/.ethereum/keystore");
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
		EthereumService service = new EthereumService(exec, keystore);

		EthWallet wallet = EthWallet.loadWalletFromString("{\"address\":\"3b4277a7d0314fb70a2afab8c1f94bc20375f33f\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"d5999ea5d1d81fa0c3218a7b02b93db18c7394ca6ecab4e96a9bee4c82573db9\",\"cipherparams\":{\"iv\":\"fb31d04c24a3dee4c31db9271d65f6c4\"},\"kdf\":\"pbkdf2\",\"kdfparams\":{\"prf\":\"hmac-sha256\",\"c\":262144,\"salt\":\"22896ce41107899bf960547affd091ee3101bd242219956ba4331e574faffeea\",\"dklen\":32},\"mac\":\"2bb16bb444159e743b3fde61bf09d9ba8f10c38c1fa15f934fa2046ad2f67c29\"},\"id\":\"ad94ca92-1dbe-457d-ba10-9aeadbd96e26\",\"version\":3}");

		wallet.unlock("secret");
		
		String info = "{\"abiDefinition\":[{\"constant\":false,\"inputs\":[],\"name\":\"kill\",\"outputs\":[],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"greet\",\"outputs\":[{\"name\": \"\",\"type\":\"string\"}],\"type\":\"function\"},{\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"}],\"type\":\"constructor\"}],\"compilerVersion\":\"0.2.0\",\"language\":\"Solidity\",\"languageVersion\":\"0.2.0\"}";
		String code = "606060405260405161023e38038061023e8339810160405280510160008054600160a060020a031916331790558060016000509080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10609f57805160ff19168380011785555b50608e9291505b8082111560cc57600081558301607d565b50505061016e806100d06000396000f35b828001600101855582156076579182015b82811115607657825182600050559160200191906001019060b0565b509056606060405260e060020a600035046341c0e1b58114610026578063cfae321714610068575b005b6100246000543373ffffffffffffffffffffffffffffffffffffffff908116911614156101375760005473ffffffffffffffffffffffffffffffffffffffff16ff5b6100c9600060609081526001805460a06020601f6002600019610100868816150201909416939093049283018190040281016040526080828152929190828280156101645780601f1061013957610100808354040283529160200191610164565b60405180806020018281038252838181518152602001915080519060200190808383829060006004602084601f0104600f02600301f150905090810190601f1680156101295780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b565b820191906000526020600020905b81548152906001019060200180831161014757829003601f168201915b505050505090509056";
		
		try {
			EthSmartContractFactory factory = new EthSmartContractFactory(info, code);
			EthTransaction tx = factory.createContract(BigInteger.valueOf(1000000L), EthTransaction.defaultGasPrice, "Hello World!");
			
			BlocklinksResponse<BigInteger> response = service.getAccountNonce(wallet.getStorage().getAddress());
			byte[] encoded = tx.signWithWallet(wallet, response.getResp());
			System.out.println(CryptoUtil.byteToHex(encoded));
			BlocklinksResponse<String> txHashResponse = service.sendTransaction(wallet,tx);
			
			String txHash =  txHashResponse.getResp();
			System.out.println("Sent transaction " + txHash);
			
			service.listenForTxReceipt(txHash, new BlocklinksCallable<TransactionReceipt>() {

				@Override
				public void call(BlocklinksResponse<TransactionReceipt> response) {
					if (response.getErrType() != null) {
						System.out.println("Error waiting for tx receipt " + response.getEx().getMessage() + " error " + response.getErrType().name());
					} else {
					
						System.out.println("Tx mined, receipt: " + response.getResp().toString());
						
						EthSmartContract contract = factory.getContract(response.getResp().getContractAddress());
						
						try {
							String greeting = contract.callConstantMethod(service.getRpcClient(), "greet");
							System.out.println("Greeting is " + greeting);
						} catch (NoSuchContractMethod ex) {
							ex.printStackTrace();
						}
					}
				}
			});
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WalletLockedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
