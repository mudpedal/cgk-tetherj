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
    private static final String PASS = "pass";
    private static final String GREETING = "Greeting";

    /**
     * Setup a simple greeter contract.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String factoryAsJson =
            "{\"code\":\"0x606060405260405161033f38038061033f8339810160405280510160008"
                + "054600160a060020a0319163317905580600160005090805190602001908280546001"
                + "81600116156101000203166002900490600052602060002090601f016020900481019"
                + "282601f10609f57805160ff19168380011785555b50608e9291505b8082111560cc57"
                + "600081558301607d565b50505061026f806100d06000396000f35b828001600101855"
                + "582156076579182015b82811115607657825182600050559160200191906001019060"
                + "b0565b509056606060405260e060020a600035046341c0e1b58114610031578063c1d"
                + "cab1414610073578063cfae321714610130575b005b61002f6000543373ffffffffff"
                + "ffffffffffffffffffffffffffffff908116911614156101ff5760005473fffffffff"
                + "fffffffffffffffffffffffffffffff16ff5b60206004803580820135601f81018490"
                + "049093026080908101604052606084815261002f94602493919291840191819083828"
                + "08284375094965050505050505060018054825160008390527fb10e2d527612073b26"
                + "eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf66020601f600260001961010"
                + "08789161502019095169490940484010481019260809083901061020657805160ff19"
                + "168380011785555b506102019291505b808211156102365760008155830161011d565"
                + "b610191600060609081526001805460a06020601f6002600019610100868816150201"
                + "909416939093049283018190040281016040526080828152929190828280156102655"
                + "780601f1061023a57610100808354040283529160200191610265565b604051808060"
                + "200182810382528381815181526020019150805190602001908083838290600060046"
                + "02084601f0104600f02600301f150905090810190601f1680156101f1578082038051"
                + "6001836020036101000a031916815260200191505b509250505060405180910390f35"
                + "b565b505050565b82800160010185558215610115579182015b828111156101155782"
                + "51826000505591602001919060010190610218565b5090565b8201919060005260206"
                + "00020905b81548152906001019060200180831161024857829003601f168201915b50"
                + "5050505090509056\",\"info\":{\"language\":\"Solidity\","
                + "\"languageVersion\":\"0.2.0\",\"compilerVersion\":\"0.2.0\","
                + "\"compilerOptions\":\"\",\"abiDefinition\":[{\"constant\":false,"
                + "\"name\":\"kill\",\"inputs\":[],\"outputs\":[],"
                + "\"type\":\"function\"},{\"constant\":false,\"name\":\"change\","
                + "\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"}],"
                + "\"outputs\":[],\"type\":\"function\"},{\"constant\":true,"
                + "\"name\":\"greet\",\"inputs\":[],"
                + "\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],"
                + "\"type\":\"function\"},{\"constant\":false,"
                + "\"inputs\":[{\"name\":\"_greeting\",\"type\":\"string\"}],"
                + "\"type\":\"constructor\"}],\"source\":\"contract mortal { "
                + "address owner; function mortal() { owner = msg.sender; } "
                + "function kill() { if (msg.sender == owner) suicide(owner); } } "
                + "contract greeterWithChange is mortal { string greeting; function "
                + "greeterWithChange(string _greeting) public { greeting = _greeting; } "
                + "function change(string _greeting) public { greeting = _greeting; } "
                + "function greet() constant returns (string) { return greeting; } "
                + "}\\n\",\"userDoc\":{\"methods\":{\"methods\":{}}},\"developerDoc\":"
                + "{\"methods\":{\"methods\":{}}}}}}";
        try {
            factory = EthSmartContractFactory.createFactoryFromContractDataString(factoryAsJson);
        } catch (Exception exception) {
            fail(exception.getMessage());
        }

    }

    @Test
    public void testFactoryInstantiation() {

        BigInteger nonce = BigInteger.ONE;

        assertTrue(factory != null);
        assertTrue(factory.getConstantFunction("greet") != null);
        assertTrue(factory.getConstantFunction("greet").inputs.length == 0);
        assertTrue(factory.getConstantFunction("greet").outputs.length == 1);
        assertTrue(factory.getConstructor() != null);
        assertTrue(factory.getConstructor().inputs.length == 1);

        EthTransaction tx = factory.createContract(GREETING);
        assertTrue(tx.getData() != null);
        assertTrue(tx.getData().length > 0);

        EthWallet wallet = EthWallet.createWallet(PASS);
        assertTrue(wallet.unlock(PASS));

        EthSignedTransaction signedTx;
        try {
            signedTx = tx.signWithWallet(wallet, nonce);
            assertTrue(signedTx.getSignedEncodedData() != null);
            assertTrue(signedTx.getSignedEncodedData().length > 0);
        } catch (WalletLockedException ex) {
            fail("Wallet is locked!");
        }
    }

}
