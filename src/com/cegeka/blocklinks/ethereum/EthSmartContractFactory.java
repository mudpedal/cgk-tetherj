package com.cegeka.blocklinks.ethereum;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import org.ethereum.core.CallTransaction.Function;
import org.ethereum.util.ByteUtil;

import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.pojo.ContractAbiMethod;
import com.cegeka.blocklinks.ethereum.pojo.ContractInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EthSmartContractFactory {
	private ContractInfo contractInfo;
	private String code;
	private HashMap<String, Function> modFunctions;
	private HashMap<String, Function> constFunctions;
	private Function constructor;

	public EthSmartContractFactory(String info, String code)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		contractInfo = mapper.readValue(info, ContractInfo.class);
		this.code = code;
		this.modFunctions = new HashMap<>();
		this.constFunctions = new HashMap<>();
		indexMethods();
	}

	private void indexMethods() {
		ContractAbiMethod[] methods = contractInfo.getAbiDefinition();
		
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
		} catch(JsonProcessingException ex) {
			ex.printStackTrace();
		}
	}

	public ContractInfo getContractInfo() {
		return contractInfo;
	}

	public String getContractInfoAsString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(contractInfo);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getCode() {
		return code;
	}

	public EthTransaction createContract(BigInteger gasLimit, BigInteger gasPrice, Object ... args) {
		
		byte[] codeBytes = CryptoUtil.hexToBytes(this.code);
		byte[] constructorCall = constructor.encodeArguments(args);
		
		return new EthTransaction(null, BigInteger.ZERO, gasPrice, gasLimit, ByteUtil.merge(codeBytes, constructorCall));
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