package com.cegeka.blocklinks.ethereum;

import com.cegeka.blocklinks.ethereum.pojo.Block;
import com.cegeka.blocklinks.ethereum.pojo.CompileOutput;
import com.cegeka.blocklinks.ethereum.pojo.Transaction;
import com.cegeka.blocklinks.ethereum.pojo.TransactionCall;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;

/**
 * Rpc Interface to use by json rpc invoker. All methods defined as per ethereum
 * rpc standard.
 * https://github.com/ethereum/wiki/wiki/JSON-RPC
 * 
 * @author Andrei Grigoriu
 *
 */
public interface EthRpcInterface {
	String eth_getBalance(String address);

	String[] eth_accounts();

	Transaction eth_getTransactionByHash(String txhash);

	TransactionReceipt eth_getTransactionReceipt(String txhash);

	String eth_sendTransaction(Transaction t);

	String eth_sendRawTransaction(String encoded);

	String eth_coinbase();

	String eth_getTransactionCount(String address, String state);

	Block eth_getBlockByNumber(Object string, Boolean full);

	String eth_call(TransactionCall txCall);

	CompileOutput eth_compileSolidity(String sourceCode);

	/**
	 * experimental, remote unlocking
	 */
	boolean personal_unlockAccount(String account, String secret);
}
