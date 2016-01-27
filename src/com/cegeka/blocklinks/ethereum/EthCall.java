package com.cegeka.blocklinks.ethereum;

import org.ethereum.core.CallTransaction.Function;

import com.cegeka.blocklinks.ethereum.pojo.TransactionCall;

public class EthCall extends TransactionCall {

	public EthCall(Function methodFunction) {
		super(methodFunction);
	}
}
