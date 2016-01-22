package com.cegeka.blocklinks.ethereum;

import com.cegeka.blocklinks.ethereum.pojo.Transaction;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;

public interface EthRpcInterface {
	String 				eth_getBalance(String address);
	String[] 			eth_accounts();
	Transaction 		eth_getTransactionByHash(String txhash);
	TransactionReceipt 	eth_getTransactionReceipt(String txhash);
	String 				eth_sendTransaction(Transaction t);
	String 				eth_sendRawTransaction(String encoded);
	String 				eth_coinbase();
	String				eth_getTransactionCount(String address, String state);
	
	/** 
	 * experimental, remote unlocking
	 */
	boolean 			personal_unlockAccount(String account, String secret);
}
