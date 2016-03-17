package com.cegeka.tetherj;

import java.math.BigInteger;

import com.cegeka.tetherj.pojo.Block;
import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.FilterLogObject;
import com.cegeka.tetherj.pojo.FilterLogRequest;
import com.cegeka.tetherj.pojo.Transaction;
import com.cegeka.tetherj.pojo.TransactionCall;
import com.cegeka.tetherj.pojo.TransactionReceipt;

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
	
	String eth_newFilter(FilterLogRequest request);
	
	String eth_newPendingTransactionFilter();
	
	Boolean eth_uninstallFilter(BigInteger filterId);
	
	Object eth_getFilterChanges(BigInteger filterId);

	/**
	 * experimental, remote unlocking
	 */
	boolean personal_unlockAccount(String account, String secret);
}
