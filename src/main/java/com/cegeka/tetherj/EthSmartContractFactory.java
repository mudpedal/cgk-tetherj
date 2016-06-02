package com.cegeka.tetherj;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;

import org.ethereum.core.CallTransaction.Function;
import org.ethereum.util.ByteUtil;

import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.pojo.ContractAbiMethod;
import com.cegeka.tetherj.pojo.ContractData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory to instantiate contracts on the chain and access existing ones.
 *
 * @author Andrei Grigoriu
 *
 */
public class EthSmartContractFactory implements Serializable {
    /**
     * .
     */
    private static final long serialVersionUID = 8576482271936503680L;
    private ContractData contract;

    private HashMap<String, Function> modFunctions;
    private HashMap<String, Function> constFunctions;
    private HashMap<String, Function> events;
    private Function constructor;

    /**
     * Construct factory by contract data.
     *
     * @param contract
     *            Contract data to use for this factory
     */
    public EthSmartContractFactory(ContractData contract) {
        this.contract = contract;
        indexMethods();
    }

    /**
     * Noarg constructor.
     */
    public EthSmartContractFactory() {
    }

    /**
     * Factory static method to create a smart contract factory from json contract data.
     *
     * @return smart contract factory.
     */
    public static EthSmartContractFactory createFactoryFromContractDataString(String json)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return new EthSmartContractFactory(mapper.readValue(json, ContractData.class));
    }

    /**
     * called once to index functions by type.
     */
    private void indexMethods() {
        this.modFunctions = new HashMap<>();
        this.constFunctions = new HashMap<>();

        ContractAbiMethod[] methods = contract.getInfo().getAbiDefinition();

        try {
            for (ContractAbiMethod method : methods) {
                ObjectMapper mapper = new ObjectMapper();
                Function function = Function.fromJsonInterface(mapper.writeValueAsString(method));
                if (method.getType().equals("constructor")) {
                    constructor = function;
                } else if (method.getType().equals("function")) {
                    if (!method.isConstant()) {
                        modFunctions.put(method.getName(), function);
                    } else {
                        constFunctions.put(method.getName(), function);
                    }
                } else {
                    events.put(method.getName(), function);
                }
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return Returns contract data.
     */
    public ContractData getContractData() {
        return contract;
    }

    /**
     * Set contract data.
     *
     * @param contract
     *            Contract data to set.
     */
    public void setContract(ContractData contract) {
        this.contract = contract;
    }

    /**
     *
     * @return Returns contract data as json string. Use this for storage purposes.
     */
    public String getContractDataAsString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(contract);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * @return Returns contract evm code.
     */
    public String getCode() {
        return contract.getCode();
    }

    /**
     * Creates a transaction that will create a contract instance.
     *
     * @param args
     *            for the contract constructor
     * @return Returns transaction to sign and submit to your service.
     */
    public EthTransaction createContract(Object... args) {

        byte[] codeBytes = CryptoUtil.hexToBytes(contract.getCode());
        byte[] constructorCall = constructor.encodeArguments(args);

        return new EthTransaction(null, BigInteger.ZERO, EthTransaction.DEFAULT_GAS_PRICE,
                EthTransaction.DEFAULT_GAS_LIMIT, ByteUtil.merge(codeBytes, constructorCall));
    }

    /**
     * Factory method to create a smart contract handle for a contract on your chain.
     *
     * @param contractAddress
     *            Contract address on the blockchain.
     * @return Returns the smart contract handle
     */
    public EthSmartContract getContract(String contractAddress) {
        return new EthSmartContract(this, contractAddress);
    }

    /**
     * @return Returns all modifier functions.
     */
    public Collection<Function> getModFunctions() {
        if (modFunctions == null) {
            indexMethods();
        }
        return modFunctions.values();
    }

    /**
     * @return Returns all constant functions.
     */
    public Collection<Function> getConstFunctions() {
        if (constFunctions == null) {
            indexMethods();
        }
        return constFunctions.values();
    }

    /**
     * @return Returns all event functions.
     */
    public Collection<Function> getEventFunctions() {
        if (events == null) {
            indexMethods();
        }
        return events.values();
    }

    /**
     * @return Returns constructor function.
     */
    public Function getConstructor() {
        if (constructor == null) {
            indexMethods();
        }
        return constructor;
    }

    /**
     * Return a constant function handle for this contract.
     *
     * @param method
     *            Method name.
     * @return Returns the requested constant function.
     */
    public Function getConstantFunction(String method) {
        if (constFunctions == null) {
            indexMethods();
        }
        return constFunctions.get(method);
    }

    /**
     * Return a modifier function handle for this contract.
     *
     * @param method
     *            Method name.
     * @return Returns the requested modifier function.
     */
    public Function getModFunction(String method) {
        if (modFunctions == null) {
            indexMethods();
        }
        return modFunctions.get(method);
    }

    /**
     * Return an event function handle for this contract.
     *
     * @param event
     *            Event name.
     * @return Returns the requested event function.
     */
    public Function getEventFunction(String event) {
        if (events == null) {
            indexMethods();
        }
        return events.get(event);
    }

}
