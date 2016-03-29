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
     * 
     */
    private static final long serialVersionUID = 8576482271936503680L;
    private ContractData contract;

    private HashMap<String, Function> modFunctions;
    private HashMap<String, Function> constFunctions;
    private Function constructor;

    /**
     * 
     * @param contract
     *            data to use for this factory
     */
    public EthSmartContractFactory(ContractData contract) {
        this.contract = contract;
        indexMethods();
    }

    /**
     * Simple constructor
     */
    public EthSmartContractFactory() {
    }

    /**
     * 
     * @return smart contract factory from json string of contract data
     */
    public static EthSmartContractFactory createFactoryFromContractDataString(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new EthSmartContractFactory(mapper.readValue(json, ContractData.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * called once to index functions by type
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
                } else {
                    if (!method.isConstant()) {
                        modFunctions.put(method.getName(), function);
                    } else {
                        constFunctions.put(method.getName(), function);
                    }
                }
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @return contact data
     */
    public ContractData getContract() {
        return contract;
    }

    /**
     * Set contract data
     * 
     * @param contract
     */
    public void setContract(ContractData contract) {
        this.contract = contract;
    }

    /**
     * 
     * @return contract data as json string. Use this to store it into the database.
     */
    public String getContractDataAsString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(contract);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return contract evm code
     */
    public String getCode() {
        return contract.getCode();
    }

    /**
     * Creates a transaction that will create a contract instance.
     * 
     * @param args
     *            for the contract constructor
     * @return transaction to sign and submit to your service
     */
    public EthTransaction createContract(Object... args) {

        byte[] codeBytes = CryptoUtil.hexToBytes(contract.getCode());
        byte[] constructorCall = constructor.encodeArguments(args);

        return new EthTransaction(null, BigInteger.ZERO, EthTransaction.defaultGasPrice,
                EthTransaction.defaultGasLimit, ByteUtil.merge(codeBytes, constructorCall));
    }

    /**
     * Create a smart contract handle for a contract on your chain.
     * 
     * @param contractAddress
     *            for the contract on the chain
     * @return the smart contract handle
     */
    public EthSmartContract getContract(String contractAddress) {
        return new EthSmartContract(this, contractAddress);
    }

    public Collection<Function> getModFunctions() {
        if (modFunctions == null) {
            indexMethods();
        }
        return modFunctions.values();
    }

    public Collection<Function> getConstFunctions() {
        if (constFunctions == null) {
            indexMethods();
        }
        return constFunctions.values();
    }

    public Function getConstructor() {
        if (constructor == null) {
            indexMethods();
        }
        return constructor;
    }

    /**
     * Return a constant function handle for this contract
     * 
     * @param method
     *            to get
     * @return
     */
    public Function getConstantFunction(String method) {
        if (constFunctions == null) {
            indexMethods();
        }
        return constFunctions.get(method);
    }

    /**
     * Return a modifier function handle for this contract
     * 
     * @param method
     *            to get
     * @return
     */
    public Function getModFunction(String method) {
        if (modFunctions == null) {
            indexMethods();
        }
        return modFunctions.get(method);
    }

}