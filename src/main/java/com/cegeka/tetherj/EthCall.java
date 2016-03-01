package com.cegeka.tetherj;

import java.math.BigInteger;

import com.cegeka.tetherj.pojo.TransactionCall;

/**
 * Call or DryCall container for function executions.
 * 
 * @author Andrei Grigoriu
 *
 */
public class EthCall {

	private TransactionCall call;

	public EthCall(TransactionCall call) {
		this.call = call;
	}

	/**
	 * @return internal call pojo
	 */
	public TransactionCall getCall() {
		return call;
	}

	/**
	 * Set a new gas limit for this call (to simulate gas)
	 * 
	 * @param gasLimit
	 */
	public void setGasLimit(BigInteger gasLimit) {
		call.setGas("0x" + gasLimit.toString(16));
	}

	/**
	 * Set a new from address, to simulate privileges (usually)
	 * 
	 * @param from
	 */
	public void setFrom(String from) {
		call.setFrom(from);
	}

	/**
	 * Set a new from address, to simulate privileges (usually)
	 * 
	 * @param from
	 */
	public void setFrom(EthWallet from) {
		call.setFrom(from.getAddress());
	}

	/**
	 * Decode output received from ethereum client
	 * 
	 * @param output
	 * @return
	 */
	public Object[] decodeOutput(String output) {
		return call.decodeOutput(output);
	}
}
