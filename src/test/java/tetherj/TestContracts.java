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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestContracts {

	private static EthSmartContractFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String factoryAsJson = "{\"contract\":{\"code\":\"0x60606040526040516103fc3803806103fc83398101604052805160805191019060008054600160a060020a031916331790558160016000509080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1060af57805160ff19168380011785555b5060939291505b8082111560dc576000815583016082565b505080600260005081905550505061031c806100e06000396000f35b82800160010185558215607b579182015b82811115607b57825182600050559160200191906001019060c0565b509056606060405260e060020a600035046341c0e1b5811461003157806382884df414610073578063cfae321714610130575b005b61002f6000543373ffffffffffffffffffffffffffffffffffffffff9081169116141561020f5760005473ffffffffffffffffffffffffffffffffffffffff16ff5b60206004803580820135601f81018490049093026080908101604052606084815261019a946024939192918401918190838280828437509496505093359350505050602060405190810160405280600081526020015060008360016000509080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024e57805160ff19168380011785555b5061027e9291505b808211156102de57848155830161011e565b61019a60006060818152600280546001805460a06020601f600019610100858716150201909316959095049182018590049094028401604052608081815294959491938491908282801561030d5780601f106102e25761010080835404028352916020019161030d565b60405180806020018381526020018281038252848181518152602001915080519060200190808383829060006004602084601f0104600f02600301f150905090810190601f1680156102005780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b565b820191906000526020600020905b81548152906001019060200180831161021f57829003601f168201915b50505050509150915091509250929050565b82800160010185558215610116579182015b82811115610116578251826000505591602001919060010190610260565b506002848155604080518354602081861615610100026000190190911693909304601f810184900484028201840190925281815286929091849183018282801561023c5780601f106102115761010080835404028352916020019161023c565b5090565b820191906000526020600020905b8154815290600101906020018083116102f057829003601f168201915b5050505050915091509150909156\",\"info\":{\"language\":\"Solidity\",\"languageVersion\":\"0.2.0\",\"compilerVersion\":\"0.2.0\",\"compilerOptions\":\"--bin --abi --userdoc --devdoc --add-std --optimize -o /tmp/solc797610747\",\"abiDefinition\":[{\"constant\":false,\"name\":\"kill\",\"inputs\":[],\"outputs\":[],\"type\":\"function\"},{\"constant\":false,\"name\":\"setGreeting\",\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"},{\"name\":\"_p\",\"type\":\"uint256\"}],\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":true,\"name\":\"greet\",\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"},{\"name\":\"_p\",\"type\":\"uint256\"}],\"type\":\"constructor\"}],\"source\":\"contract mortal { address owner; function mortal() { owner = msg.sender; } /// @notice Will kill contract \\n function kill() { if (msg.sender == owner) suicide(owner); } } contract greeter is mortal { string greeting; uint256 p; function greeter(string _greeting, uint256 _p) public { greeting = _greeting; p=_p; } /// @notice Will setGreeting to `_greeting` \\n /// @dev This is the developer documentation. \\n /// @param _greeting greeting to say. \\n /// @param _p number to store. \\n  function setGreeting(string _greeting, uint256 _p) public returns(string, uint256)  { greeting = _greeting; p=_p; return (greeting, p);} function greet() constant returns (string, uint256) { return (greeting,p); } }\",\"userDoc\":{\"methods\":{\"methods\":{\"setGreeting(string,uint256)\":{\"notice\":\"Will setGreeting to `_greeting` \"},\"kill()\":{\"notice\":\"Will kill contract \"}}}},\"developerDoc\":{\"methods\":{\"methods\":{\"setGreeting(string,uint256)\":{\"details\":\"This is the developer documentation. \",\"params\":{\"params\":{\"_p\":\"number to store. \",\"_greeting\":\"greeting to say. \"}}}}}}}},\"modFunctions\":{\"setGreeting\":{\"constant\":false,\"name\":\"setGreeting\",\"inputs\":[{\"name\":\"_greeting\",\"type\":{\"_class\":\"org.ethereum.core.CallTransaction$StringType\",\"name\":\"string\"}},{\"name\":\"_p\",\"type\":{\"_class\":\"org.ethereum.core.CallTransaction$IntType\",\"name\":\"uint256\"}}],\"outputs\":[{\"name\":\"\",\"type\":{\"_class\":\"org.ethereum.core.CallTransaction$StringType\",\"name\":\"string\"}},{\"name\":\"\",\"type\":{\"_class\":\"org.ethereum.core.CallTransaction$IntType\",\"name\":\"uint256\"}}],\"type\":\"function\"},\"kill\":{\"constant\":false,\"name\":\"kill\",\"inputs\":[],\"outputs\":[],\"type\":\"function\"}},\"constFunctions\":{\"greet\":{\"constant\":true,\"name\":\"greet\",\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":{\"_class\":\"org.ethereum.core.CallTransaction$StringType\",\"name\":\"string\"}},{\"name\":\"\",\"type\":{\"_class\":\"org.ethereum.core.CallTransaction$IntType\",\"name\":\"uint256\"}}],\"type\":\"function\"}},\"constructor\":{\"constant\":false,\"inputs\":[{\"name\":\"_greeting\",\"type\":{\"_class\":\"org.ethereum.core.CallTransaction$StringType\",\"name\":\"string\"}},{\"name\":\"_p\",\"type\":{\"_class\":\"org.ethereum.core.CallTransaction$IntType\",\"name\":\"uint256\"}}],\"type\":\"constructor\"}}";
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			factory = mapper.readValue(factoryAsJson, EthSmartContractFactory.class);
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void testFactoryInstantiation() {
		
		String pass = "pass";
		String constArg0 = "Greeting";
		BigInteger constArg1 = BigInteger.TEN;
		BigInteger nonce = BigInteger.ONE;
		
		assertTrue(factory != null);
		assertTrue(factory.getConstantFunction("greet") != null);
		assertTrue(factory.getConstantFunction("greet").inputs.length == 0);
		assertTrue(factory.getConstantFunction("greet").outputs.length == 2);
		assertTrue(factory.getModFunction("setGreeting") != null);
		assertTrue(factory.getModFunction("setGreeting").inputs.length == 2);
		assertTrue(factory.getModFunction("setGreeting").outputs.length == 2);
		
		
		EthTransaction tx = factory.createContract(constArg0, constArg1);
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
