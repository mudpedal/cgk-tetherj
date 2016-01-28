package com.cegeka.test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.ethereum.core.CallTransaction;
import org.ethereum.util.DecodeResult;
import org.ethereum.util.RLP;
import org.ethereum.util.RLPElement;
import org.ethereum.util.RLPList;

import com.cegeka.blocklinks.api.BlocklinksCallable;
import com.cegeka.blocklinks.api.BlocklinksResponse;
import com.cegeka.blocklinks.api.EthereumService;
import com.cegeka.blocklinks.api.WalletLockedException;
import com.cegeka.blocklinks.ethereum.EthCall;
import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthSmartContract;
import com.cegeka.blocklinks.ethereum.EthSmartContractFactory;
import com.cegeka.blocklinks.ethereum.EthTransaction;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.NoSuchContractMethod;
import com.cegeka.blocklinks.ethereum.Util;
import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.pojo.ContractData;
import com.cegeka.blocklinks.ethereum.pojo.ContractInfo;
import com.cegeka.blocklinks.ethereum.pojo.CompileOutput;
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
		 * File keystore = new File("/home/andreicg/.ethereum/keystore");
		 * ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
		 * EthereumService service = new EthereumService(exec, keystore);
		 * 
		 * EthWallet wallet = EthWallet.loadWalletFromString(
		 * "{\"address\":\"3b4277a7d0314fb70a2afab8c1f94bc20375f33f\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"d5999ea5d1d81fa0c3218a7b02b93db18c7394ca6ecab4e96a9bee4c82573db9\",\"cipherparams\":{\"iv\":\"fb31d04c24a3dee4c31db9271d65f6c4\"},\"kdf\":\"pbkdf2\",\"kdfparams\":{\"prf\":\"hmac-sha256\",\"c\":262144,\"salt\":\"22896ce41107899bf960547affd091ee3101bd242219956ba4331e574faffeea\",\"dklen\":32},\"mac\":\"2bb16bb444159e743b3fde61bf09d9ba8f10c38c1fa15f934fa2046ad2f67c29\"},\"id\":\"ad94ca92-1dbe-457d-ba10-9aeadbd96e26\",\"version\":3}"
		 * );
		 * 
		 * wallet.unlock("secret"); String to =
		 * "0x5cc3a427f9c91781625ea36fa3b2f71baa8467bb"; BigInteger wei =
		 * Util.fromEtherToWei(BigDecimal.valueOf(1.2));
		 * 
		 * EthTransaction tx = new EthTransaction(to, wei);
		 * service.sendTransaction(wallet, tx, new BlocklinksCallable<String> ()
		 * {
		 * 
		 * @Override public void call(BlocklinksResponse<String> response) { if
		 * (response.getErrType() == null) { String txHash = response.getResp();
		 * System.out.println("Sent transaction " + txHash);
		 * 
		 * service.listenForTxReceipt(txHash, new
		 * BlocklinksCallable<TransactionReceipt>() {
		 * 
		 * @Override public void call(BlocklinksResponse<TransactionReceipt>
		 * response) { if (response.getErrType() != null) { System.out.println(
		 * "Error waiting for tx receipt " + response.getEx().getMessage() +
		 * " error " + response.getErrType().name()); } else {
		 * System.out.println("Tx mined, receipt: " +
		 * response.getResp().toString()); } } }); } else { System.out.println(
		 * "Failed to send, error " + response.getErrType().name() + " ex " +
		 * response.getEx().getMessage()); } }
		 * 
		 * });
		 * 
		 * service.getAccounts(new BlocklinksCallable<String[]>() {
		 * 
		 * @Override public void call(BlocklinksResponse<String[]> response) {
		 * if (response.getErrType() != null) { System.out.println("GOT ERROR "
		 * + response.getErrType().name()); } else { System.out.println(
		 * "GOT response " + Arrays.toString(response.getResp())); } } });
		 */

		EthereumService service = new EthereumService();

		EthWallet wallet = EthWallet.loadWalletFromString(
				"{\"address\":\"3b4277a7d0314fb70a2afab8c1f94bc20375f33f\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"d5999ea5d1d81fa0c3218a7b02b93db18c7394ca6ecab4e96a9bee4c82573db9\",\"cipherparams\":{\"iv\":\"fb31d04c24a3dee4c31db9271d65f6c4\"},\"kdf\":\"pbkdf2\",\"kdfparams\":{\"prf\":\"hmac-sha256\",\"c\":262144,\"salt\":\"22896ce41107899bf960547affd091ee3101bd242219956ba4331e574faffeea\",\"dklen\":32},\"mac\":\"2bb16bb444159e743b3fde61bf09d9ba8f10c38c1fa15f934fa2046ad2f67c29\"},\"id\":\"ad94ca92-1dbe-457d-ba10-9aeadbd96e26\",\"version\":3}");

		wallet.unlock("secret");

		String mortal = "contract mortal { address owner; function mortal() { owner = msg.sender; } /// @notice Will kill this guy. function kill() { if (msg.sender == owner) suicide(owner); } }";
		String sourceCode = "contract mortal { address owner; function mortal() { owner = msg.sender; } /// @notice Will kill contract \n function kill() { if (msg.sender == owner) suicide(owner); } } contract greeter is mortal { string greeting; uint256 p; function greeter(string _greeting, uint256 _p) public { greeting = _greeting; p=_p; } /// @notice Will setGreeting to `_greeting` \n /// @dev This is the developer documentation. \n /// @param _greeting greeting to say. \n /// @param _p number to store. \n  function setGreeting(string _greeting, uint256 _p) public returns(string, uint256)  { greeting = _greeting; p=_p; return (greeting, p);} function greet() constant returns (string, uint256) { return (greeting,p); } }";
		//String info = "{\"abiDefinition\":[{\"constant\":false,\"inputs\":[],\"name\":\"kill\",\"outputs\":[],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"},{\"name\":\"_p\",\"type\":\"uint256\"}],\"name\":\"setGreeting\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"greet\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"},{\"name\":\"_p\",\"type\":\"uint256\"}],\"type\":\"constructor\"}],\"compilerVersion\":\"0.2.0\",\"language\":\"Solidity\",\"languageVersion\":\"0.2.0\"}";
		//String code = "60606040526040516105b63803806105b6833981016040528080518201919060200180519060200190919050505b5b33600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908302179055505b8160016000509080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a957805160ff19168380011785556100da565b828001600101855582156100da579182015b828111156100d95782518260005055916020019190600101906100bb565b5b50905061010591906100e7565b8082111561010157600081815060009055506001016100e7565b5090565b5050806002600050819055505b5050610494806101226000396000f360606040526000357c01000000000000000000000000000000000000000000000000000000009004806341c0e1b51461004f57806382884df41461005e578063cfae3217146101305761004d565b005b61005c60048050506101b2565b005b6100bb6004808035906020019082018035906020019191908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050909091908035906020019091905050610246565b60405180806020018381526020018281038252848181518152602001915080519060200190808383829060006004602084601f0104600f02600301f150905090810190601f1680156101215780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b61013d60048050506103ca565b60405180806020018381526020018281038252848181518152602001915080519060200190808383829060006004602084601f0104600f02600301f150905090810190601f1680156101a35780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141561024357600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16ff5b5b565b602060405190810160405280600081526020015060008360016000509080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102ab57805160ff19168380011785556102dc565b828001600101855582156102dc579182015b828111156102db5782518260005055916020019190600101906102bd565b5b50905061030791906102e9565b8082111561030357600081815060009055506001016102e9565b5090565b5050826002600050819055506001600050600260005054818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103b35780601f10610388576101008083540402835291602001916103b3565b820191906000526020600020905b81548152906001019060200180831161039657829003601f168201915b50505050509150915091506103c3565b9250929050565b602060405190810160405280600081526020015060006001600050600260005054818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104805780601f1061045557610100808354040283529160200191610480565b820191906000526020600020905b81548152906001019060200180831161046357829003601f168201915b5050505050915091509150610490565b909156";

		BlocklinksResponse<CompileOutput> contractResponse = service.compileSolidity(sourceCode);
		
		if (contractResponse.getErrorType() != null) {
			System.out.println("FAILED TO COMPILED CONTRACT, reason: " + contractResponse.getException().getMessage() + " " + contractResponse.getException().getCause().toString());
			return;
		}
		
		CompileOutput contracts = contractResponse.getValue();
		String[] contractNames = contracts.getContractNames();
		System.out.println("Compiled " + contractNames.length + " contracts: " + Arrays.toString(contractNames));
		
		System.out.println("Choosing second contract named " + contractNames[1]);
		ContractData firstContract = contractResponse.getValue().getContractByName(contractNames[1]);
		
		
		try {

			Future<BlocklinksResponse<BigInteger>> responseFuture = service
					.getAccountNonceFuture(wallet.getStorage().getAddress());
			EthSmartContractFactory factory = new EthSmartContractFactory(firstContract);
			
			
			
			String factoryJson = factory.getContractDataAsString();
			EthSmartContractFactory newFactory = EthSmartContractFactory.createFactoryFromContractDataString(factoryJson);
			
			EthTransaction tx = newFactory.createContract("Hello World!", BigInteger.valueOf(500000L));
			tx.setGasLimit(EthTransaction.maximumGasLimit);

			BlocklinksResponse<BigInteger> response = responseFuture.get();
			byte[] encoded = tx.signWithWallet(wallet, response.getValue());
			System.out.println(CryptoUtil.byteToHex(encoded));
			BlocklinksResponse<String> txHashResponse = service.sendTransaction(wallet, tx);

			String txHash = txHashResponse.getValue();
			System.out.println("Sent transaction " + txHash);

			service.listenForTxReceipt(txHash, new BlocklinksCallable<TransactionReceipt>() {

				@Override
				public void call(BlocklinksResponse<TransactionReceipt> response) {
					if (response.getErrorType() != null) {
						System.out.println("Error waiting for tx receipt " + response.getException().getMessage()
								+ " error " + response.getErrorType().name());
					} else {

						System.out.println("Tx mined, receipt: " + response.getValue().toString());
						EthSmartContract contract = factory.getContract(response.getValue().getContractAddress());

						try {
							EthCall call = contract.callConstantMethod("greet");
							BlocklinksResponse<Object[]> greetingResponse = service.makeCall(call);
							Object[] greeting = greetingResponse.getValue();
							System.out.println("Greeting is " + greeting[0].toString());

							call = contract.dryCallModMethod(wallet.getStorage().getAddress(), "setGreeting",
									"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
									BigInteger.valueOf(69));
							
							call.setGasLimit(EthTransaction.maximumGasLimit);
							greetingResponse = service.makeCall(call);
							Object[] newGreeting = greetingResponse.getValue();

							if (newGreeting.length > 0) {
								System.out.println("Greeting is " + newGreeting[0].toString());
								// lets change it forever
								EthTransaction tx = contract.callModMethod("setGreeting",
										"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
										BigInteger.valueOf(69));
								tx.setGasLimit(EthTransaction.maximumGasLimit);

								BlocklinksResponse<BigInteger> newNonce = service
										.getAccountNonce(wallet.getStorage().getAddress());
								byte[] encoded = tx.signWithWallet(wallet, newNonce.getValue());
								System.out.println(CryptoUtil.byteToHex(encoded));
								BlocklinksResponse<String> txHashResponse = service.sendTransaction(wallet, tx);
								System.out.println("Sending transaction " + txHashResponse.getValue());
								service.listenForTxReceipt(txHashResponse.getValue(),
										new BlocklinksCallable<TransactionReceipt>() {

									@Override
									public void call(BlocklinksResponse<TransactionReceipt> response) {
										if (response.getErrorType() != null) {
											System.out.println("Error waiting for tx receipt "
													+ response.getException().getMessage() + " error "
													+ response.getErrorType().name());
										} else {

											System.out.println("Tx mined for set greeting, receipt: "
													+ response.getValue().toString());
											Object[] greeting;
											try {
												EthCall call = contract.callConstantMethod("greet");
												BlocklinksResponse<Object[]> greetingResponse = service.makeCall(call);
												greeting = greetingResponse.getValue();

												System.out.println("Greeting is " + greeting[0].toString());
											} catch (NoSuchContractMethod e) {
												e.printStackTrace();
											}

										}
									}
								});

							} else {
								System.out.println("No return");
							}

						} catch (NoSuchContractMethod ex) {
							ex.printStackTrace();
						} catch (WalletLockedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});

		} catch (WalletLockedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// String encoded =
		// "0x0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000c48656c6c6f20576f726c64210000000000000000000000000000000000000000";
		// CallTransaction tx = new CallTransaction();

	}
}
