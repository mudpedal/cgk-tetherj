package com.cegeka.tetherj;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ethereum.core.CallTransaction.Function;

import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.pojo.FilterLogRequest;
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
        callPojo.setFrom("0xffffffffffffffffffffffffffffffffffffffff");
        callPojo.setGas(null);
        callPojo.setGasPrice(null);
        callPojo.setTo(this.contractAddress);
        callPojo.setValue("0x0");

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
        callPojo.setValue("0x0");

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

    /**
     * Return a filter object with encoded topics.
     *
     * @param event
     *            Event name.
     * @param args
     *            Event arguments to search by, only use indexed ones.
     * @return the filter object to submit to the ethereum service.
     * @throws NoSuchContractMethod
     *             (if no such event exists)
     */
    public FilterLogRequest getEventFilter(String event, Object... args)
            throws NoSuchContractMethod {
        Function eventFunction = factory.getEventFunction(event);

        if (eventFunction == null) {
            throw new NoSuchContractMethod(
                    "Event " + event + " does not exist for contract factory");
        }

        FilterLogRequest filter = new FilterLogRequest();
        filter.setAddress(this.contractAddress);
        filter.setFromBlock("latest");
        filter.setToBlock("latest");
        filter.setTopics(eventFunction.encodeTopics(args));
        filter.setFunction(eventFunction);

        logger.debug(
                "Generated event filter call (contractAddress: " + this.contractAddress + ", name: "
                        + event + ", params: " + Arrays.toString(args) + ")" + filter.toString());

        return filter;
    }
}
