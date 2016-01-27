package com.cegeka.blocklinks.ethereum;
import java.math.BigInteger;

import com.cegeka.blocklinks.ethereum.pojo.TransactionCall;

public class EthCall {

	private TransactionCall call;
	
	public EthCall(TransactionCall call) {
		this.call = call;
	}
	
	public TransactionCall getCall() {
		return call;
	}
	
	public void setGasLimit(BigInteger gasLimit){
		call.setGas("0x" + gasLimit.toString(16));
	}
	
	public void setFrom(String from){
		call.setFrom(from);
	}
	
	public void setFrom(EthWallet from){
		call.setFrom(from.getAddress());
	}
	
	public Object[] decodeOutput(String output) {
		return call.decodeOutput(output);
	}
}
