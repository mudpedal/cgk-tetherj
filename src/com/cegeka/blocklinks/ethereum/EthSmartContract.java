package com.cegeka.blocklinks.ethereum;

import java.math.BigInteger;
import java.util.Arrays;

import org.ethereum.core.CallTransaction.Function;

import com.cegeka.blocklinks.ethereum.crypto.CryptoUtil;
import com.cegeka.blocklinks.ethereum.pojo.TransactionCall;

public class EthSmartContract {
	private EthSmartContractFactory factory;
	private String contractAddress;
	
	public EthSmartContract (EthSmartContractFactory factory, String contractAddress) {
		this.factory = factory;
		this.contractAddress = contractAddress;
	}
	
	public Object[] callConstantMethod(EthRpcClient rpc, String method, Object ... args) throws NoSuchContractMethod {
		Function methodFunction = factory.getConstantFunction(method);
		
		if (methodFunction == null) {
			throw new NoSuchContractMethod("Method " + method + " does not exist for contract factory");
		}
		
		TransactionCall call = new TransactionCall();
		call.setData(CryptoUtil.byteToHexWithPrefix(methodFunction.encode(args)));
		call.setFrom(null);
		call.setGas(null);
		call.setGasPrice(null);
		call.setTo(this.contractAddress);
		call.setValue(null);
		
		String response = rpc.callMethod(call);
		
		Object[] decodedOutputs = methodFunction.decodeResult(CryptoUtil.hexToBytes(response));
		return decodedOutputs;
	}
	
	public Object[] dryCallModMethod(String from, EthRpcClient rpc, String method, Object ... args) throws NoSuchContractMethod {
		Function methodFunction = factory.getModFunction(method);
		
		if (methodFunction == null) {
			throw new NoSuchContractMethod("Method " + method + " does not exist for contract factory");
		}
		
		TransactionCall call = new TransactionCall();
		call.setData(CryptoUtil.byteToHexWithPrefix(methodFunction.encode(args)));
		call.setFrom(from);
		call.setGas(null);
		call.setGasPrice(null);
		call.setTo(this.contractAddress);
		call.setValue(null);
		
		String response = rpc.callMethod(call);
		
		Object[] decodedOutputs = methodFunction.decodeResult(CryptoUtil.hexToBytes(response));
		return decodedOutputs;
	}
	
	public EthTransaction callModMethod(EthWallet from, BigInteger gasLimit, String method, Object ... args) throws NoSuchContractMethod {
		Function methodFunction = factory.getModFunction(method);
		
		if (methodFunction == null) {
			throw new NoSuchContractMethod("Method " + method + " does not exist for contract factory");
		}
		
		return new EthTransaction(this.contractAddress, BigInteger.ZERO, EthTransaction.defaultGasPrice, gasLimit, methodFunction.encode(args));
	}
}
