package tetherj;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cegeka.tetherj.EthSignedTransaction;
import com.cegeka.tetherj.EthSmartContractFactory;
import com.cegeka.tetherj.EthTransaction;
import com.cegeka.tetherj.EthWallet;
import com.cegeka.tetherj.api.WalletLockedException;

public class TestContracts {

	private static EthSmartContractFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String factoryAsJson = "{\"code\":\"0x606060405260405161033f38038061033f8339810160405280510160008054600160a060020a031916331790558060016000509080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10609f57805160ff19168380011785555b50608e9291505b8082111560cc57600081558301607d565b50505061026f806100d06000396000f35b828001600101855582156076579182015b82811115607657825182600050559160200191906001019060b0565b509056606060405260e060020a600035046341c0e1b58114610031578063c1dcab1414610073578063cfae321714610130575b005b61002f6000543373ffffffffffffffffffffffffffffffffffffffff908116911614156101ff5760005473ffffffffffffffffffffffffffffffffffffffff16ff5b60206004803580820135601f81018490049093026080908101604052606084815261002f9460249391929184019181908382808284375094965050505050505060018054825160008390527fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf66020601f60026000196101008789161502019095169490940484010481019260809083901061020657805160ff19168380011785555b506102019291505b808211156102365760008155830161011d565b610191600060609081526001805460a06020601f6002600019610100868816150201909416939093049283018190040281016040526080828152929190828280156102655780601f1061023a57610100808354040283529160200191610265565b60405180806020018281038252838181518152602001915080519060200190808383829060006004602084601f0104600f02600301f150905090810190601f1680156101f15780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b565b505050565b82800160010185558215610115579182015b82811115610115578251826000505591602001919060010190610218565b5090565b820191906000526020600020905b81548152906001019060200180831161024857829003601f168201915b505050505090509056\",\"info\":{\"language\":\"Solidity\",\"languageVersion\":\"0.2.0\",\"compilerVersion\":\"0.2.0\",\"compilerOptions\":\"\",\"abiDefinition\":[{\"constant\":false,\"name\":\"kill\",\"inputs\":[],\"outputs\":[],\"type\":\"function\"},{\"constant\":false,\"name\":\"change\",\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"}],\"outputs\":[],\"type\":\"function\"},{\"constant\":true,\"name\":\"greet\",\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"}],\"type\":\"constructor\"}],\"source\":\"contract mortal { address owner; function mortal() { owner = msg.sender; } function kill() { if (msg.sender == owner) suicide(owner); } } contract greeterWithChange is mortal { string greeting; function greeterWithChange(string _greeting) public { greeting = _greeting; } function change(string _greeting) public { greeting = _greeting; } function greet() constant returns (string) { return greeting; } }\\n\",\"userDoc\":{\"methods\":{\"methods\":{}}},\"developerDoc\":{\"methods\":{\"methods\":{}}}}}}";
		
		factory = EthSmartContractFactory.createFactoryFromContractDataString(factoryAsJson);
		if (factory == null) {
			fail("Cannot instantiate factory object!");
		}

	}

	@Test
	public void testFactoryInstantiation() {
		
		String pass = "pass";
		String constArg0 = "Greeting";
		BigInteger nonce = BigInteger.ONE;
		
		assertTrue(factory != null);
		assertTrue(factory.getConstantFunction("greet") != null);
		assertTrue(factory.getConstantFunction("greet").inputs.length == 0);
		assertTrue(factory.getConstantFunction("greet").outputs.length == 1);
		assertTrue(factory.getConstructor() != null);
		assertTrue(factory.getConstructor().inputs.length == 1);
		
		
		EthTransaction tx = factory.createContract(constArg0);
		assertTrue(tx.getData() != null);
		assertTrue(tx.getData().length > 0);
		
		EthWallet wallet = EthWallet.createWallet(pass);
		assertTrue(wallet.unlock(pass));
		
		EthSignedTransaction signedTx;
		try {
			signedTx = tx.signWithWallet(wallet, nonce);
			assertTrue(signedTx.getSignedEncodedData() != null);
			assertTrue(signedTx.getSignedEncodedData().length > 0);
		} catch (WalletLockedException e) {
			fail("Wallet is locked!");
		}
	}

}
