package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;
import java.util.HashMap;

import org.ethereum.core.CallTransaction.Function;
import org.ethereum.util.ByteUtil;

import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.pojo.ContractData;
import com.cegeka.blocklinks.ethereum.pojo.ContractAbiMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory to instantiate contracts on the chain and access existing ones.
 * 
 * @author Andrei Grigoriu
 *
 */
public class EthSmartContractFactory {
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
		this.modFunctions = new HashMap<>();
		this.constFunctions = new HashMap<>();
		indexMethods();
	}

	/**
	 * called once to index functions by type
	 */
	private void indexMethods() {
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
	 * 
	 * @return contract data as json string. Use this to store it into the
	 *         database.
	 */
	public String getContractAsString() {
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

		return new EthTransaction(null, BigInteger.ZERO, EthTransaction.defaultGasPrice, EthTransaction.defaultGasLimit,
				ByteUtil.merge(codeBytes, constructorCall));
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

	/**
	 * Return a constant function handle for this contract
	 * 
	 * @param method
	 *            to get
	 * @return
	 */
	public Function getConstantFunction(String method) {
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
		return modFunctions.get(method);
	}

}