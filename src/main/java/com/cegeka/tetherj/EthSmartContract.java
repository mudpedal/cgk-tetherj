package com.cegeka.tetherj;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ethereum.core.CallTransaction.Function;

import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.pojo.TransactionCall;

/**
 * Instance to control a contract on the ethereum chain.
 *
 * @author Andrei Grigoriu
 *
 */
public class EthSmartContract {
    private EthSmartContractFactory factory;
    private String contractAddress;

    private static final Logger logger = LogManager.getLogger(EthSmartContract.class);

    /**
     * Construct from factory.
     *
     * @param factory
     *            Factory to use for method calling.
     * @param contractAddress
     *            Address of the contract.
     */
    public EthSmartContract(EthSmartContractFactory factory, String contractAddress) {
        this.factory = factory;
        this.contractAddress = contractAddress;
    }

    /**
     * Return a call that will call a constant method on this contract.
     *
     * @param method
     *            Name of the method to call.
     * @param args
     *            Method arguments, variadic.
     * @return Returns the call to submit to your service.
     * @throws NoSuchContractMethod
     *             (if no such constant method exists)
     */
    public EthCall callConstantMethod(String method, Object... args) throws NoSuchContractMethod {
        Function methodFunction = factory.getConstantFunction(method);

        if (methodFunction == null) {
            throw new NoSuchContractMethod(
                    "Method " + method + " does not exist for contract factory");
        }

        TransactionCall callPojo = new TransactionCall(methodFunction);
        callPojo.setData(CryptoUtil.byteToHexWithPrefix(methodFunction.encode(args)));
        callPojo.setFrom(null);
        callPojo.setGas(null);
        callPojo.setGasPrice(null);
        callPojo.setTo(this.contractAddress);
        callPojo.setValue(null);

        logger.debug("Generated constant call (contractAddress: " + this.contractAddress
                + ", method: " + method + ", params: " + Arrays.toString(args) + ")"
                + callPojo.toString());

        return new EthCall(callPojo);
    }

    /**
     * Return a call that will dry call a modifier method on this contract.
     *
     * @param from
     *            Address to dry call as.
     * @param method
     *            Name of method to dry call.
     * @param args
     *            Method arguments, variadic.
     * @return the call to submit to your service
     * @throws NoSuchContractMethod
     *             (if no such modifier method exists)
     */
    public EthCall dryCallModMethod(String from, String method, Object... args)
            throws NoSuchContractMethod {
        Function methodFunction = factory.getModFunction(method);

        if (methodFunction == null) {
            throw new NoSuchContractMethod(
                    "Method " + method + " does not exist for contract factory");
        }

        TransactionCall callPojo = new TransactionCall(methodFunction);
        callPojo.setData(CryptoUtil.byteToHexWithPrefix(methodFunction.encode(args)));
        callPojo.setFrom(from);
        callPojo.setGas(null);
        callPojo.setGasPrice(null);
        callPojo.setTo(this.contractAddress);
        callPojo.setValue(null);

        logger.debug("Generated dry call (contractAddress: " + this.contractAddress + ", method: "
                + method + ", params: " + Arrays.toString(args) + ")" + callPojo.toString());

        return new EthCall(callPojo);
    }

    /**
     * Returns a transaction that will call a modifier method on the contract.
     *
     * @param method
     *            Name of modifier method to call.
     * @param args
     *            Method arguments, variadic.
     * @return transaction
     * @throws NoSuchContractMethod
     *             (if no such modifier method exists)
     */
    public EthTransaction callModMethod(String method, Object... args) throws NoSuchContractMethod {
        Function methodFunction = factory.getModFunction(method);

        if (methodFunction == null) {
            throw new NoSuchContractMethod(
                    "Method " + method + " does not exist for contract factory");
        }

        EthTransaction tx = new EthTransaction(this.contractAddress, BigInteger.ZERO,
                EthTransaction.DEFAULT_GAS_PRICE, EthTransaction.DEFAULT_GAS_LIMIT,
                methodFunction.encode(args));
        logger.debug(
                "Generated function call tx(contractAddress: " + this.contractAddress + ", method: "
                        + method + ", params: " + Arrays.toString(args) + ")" + tx.toString());

        return tx;
    }

    /** TODO ADD JAVADOC **/
    public EthCall getEvents(String method, Object... args) throws NoSuchContractMethod {
        Function eventFunction = factory.getEventFunction(method);

        if (eventFunction == null) {
            throw new NoSuchContractMethod(
                "Method " + method + " does not exist for contract factory");
        }

        TransactionCall callPojo = new TransactionCall(eventFunction);
        callPojo.setData(CryptoUtil.byteToHexWithPrefix(eventFunction.encode(args)));
        callPojo.setFrom(null);
        callPojo.setGas(null);
        callPojo.setGasPrice(null);
        callPojo.setTo(this.contractAddress);
        callPojo.setValue(null);

        logger.debug("Generated event call (contractAddress: " + this.contractAddress
            + ", method: " + method + ", params: " + Arrays.toString(args) + ")"
            + callPojo.toString());

        return new EthCall(callPojo);
    }
}
