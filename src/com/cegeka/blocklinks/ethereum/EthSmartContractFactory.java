package com.cegeka.blocklinks.ethereum;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import org.ethereum.core.CallTransaction.Function;
import org.ethereum.util.ByteUtil;

import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.pojo.ContractData;
import com.cegeka.blocklinks.ethereum.pojo.ContractAbiMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EthSmartContractFactory {
	private ContractData contract;
	private HashMap<String, Function> modFunctions;
	private HashMap<String, Function> constFunctions;
	private Function constructor;

	public EthSmartContractFactory(ContractData contract){
		this.contract = contract;
		this.modFunctions = new HashMap<>();
		this.constFunctions = new HashMap<>();
		indexMethods();
	}

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

	public ContractData getContract() {
		return contract;
	}

	public String getContractAsString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(contract);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getCode() {
		return contract.getCode();
	}

	public EthTransaction createContract(Object... args) {

		byte[] codeBytes = CryptoUtil.hexToBytes(contract.getCode());
		byte[] constructorCall = constructor.encodeArguments(args);

		return new EthTransaction(null, BigInteger.ZERO, EthTransaction.defaultGasPrice, EthTransaction.defaultGasLimit,
				ByteUtil.merge(codeBytes, constructorCall));
	}

	public EthSmartContract getContract(String contractAddress) {
		return new EthSmartContract(this, contractAddress);
	}

	public Function getConstantFunction(String method) {
		return constFunctions.get(method);
	}

	public Function getModFunction(String method) {
		return modFunctions.get(method);
	}

}