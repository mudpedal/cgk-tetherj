package com.cegeka.blocklinks.api;

import java.io.File;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.EthTransaction;
import com.cegeka.blocklinks.ethereum.EthWallet;
import com.cegeka.blocklinks.ethereum.pojo.TransactionReceipt;
import com.googlecode.jsonrpc4j.HttpException;
import com.googlecode.jsonrpc4j.JsonRpcClientException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Implementation for an Ethereum service api
 *
 */
public class EthereumService {
	
	public interface RpcAction<T> {
		public T call();
	}

	public static final int receiptCheckIntervalMillis = 1000;
	public static final int receiptMaxChecks = 60 * 1000 * 10 / receiptCheckIntervalMillis;

	private final EthRpcClient rpc;
	private final ScheduledExecutorService executor;
	
	private static final Logger logger = LogManager.getLogger(EthereumService.class);

	public EthereumService(ScheduledExecutorService executor, File keystoreDir) {
		this(executor, keystoreDir, EthRpcClient.defaultHostname, EthRpcClient.defaultPort);
	}
	
	public EthereumService(ScheduledExecutorService executor, File keystoreDir, String rpcHostname) {
		this(executor, keystoreDir, rpcHostname, EthRpcClient.defaultPort);
	}
	
	public EthereumService(ScheduledExecutorService executor, File keystoreDir, String rpcHostname, int port) {
		this.executor = executor;
		rpc = new EthRpcClient(rpcHostname, port);
		logger.info("Created ethereum service");
	}
	
	public EthWallet createWallet(String passphrase) {
		return EthWallet.createWallet(passphrase);
	}
	
	public EthRpcClient getRpcClient() {
		return rpc;
	}
	
	private <T> BlocklinksResponse<T> performBlockingRpcAction(RpcAction<T> rpcAction) {
		ErrorType err = null;
		Exception e = null;
		T rpcResponse = null;
		
		try {
			rpcResponse = rpcAction.call();
		} catch (JsonRpcClientException rpcEx) {
			err = ErrorType.BLOCKCHAIN_CLIENT_OPERATION_ERROR;
			e = rpcEx;
		} catch (UndeclaredThrowableException | HttpException ex) {
			err = ErrorType.BLOCKCHAIN_CLIENT_BAD_CONNECTION;
			e = ex;
		} catch (Exception generalException) {
			err = ErrorType.UNKNOWN_ERROR;
			e = generalException;
		}
		
		return new BlocklinksResponse<T>(err, e, rpcResponse);
	}
	
	private <T> void performAsyncRpcAction(RpcAction<T> rpcAction, BlocklinksCallable<T> callable) {
		synchronized (executor) {
			try {
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					callable.call(performBlockingRpcAction(rpcAction));
				}
			});	
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void getAccounts(BlocklinksCallable<String[]> callable) {
		performAsyncRpcAction(new RpcAction<String[]>() {

			@Override
			public String[] call() {
				return rpc.getAccounts();
			}
			
		}, callable);
	}
	
	public BlocklinksResponse<String[]> getAccounts() {
		return performBlockingRpcAction(new RpcAction<String[]>() {

			@Override
			public String[] call() {
				return rpc.getAccounts();
			}
		});
	}
	
	public void getBalance(final String address, BlocklinksCallable<BigInteger> callable) {
		performAsyncRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getBalance(address);
			}
			
		}, callable);
	}
	
	public BlocklinksResponse<BigInteger> getBalance(final String address) {
		return performBlockingRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getBalance(address);
			}
		});
	}
	
	public void getAccountNonce(final String address, BlocklinksCallable<BigInteger> callable) {
		performAsyncRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getAccountNonce(address);
			}
			
		}, callable);
	}
	
	public BlocklinksResponse<BigInteger> getAccountNonce(final String address) {
		return performBlockingRpcAction(new RpcAction<BigInteger>() {

			@Override
			public BigInteger call() {
				return rpc.getAccountNonce(address);
			}
		});
	}
	
	public BlocklinksResponse<String> sendTransaction(EthWallet from, EthTransaction transaction) throws WalletLockedException {
		BlocklinksResponse<BigInteger> nonceResponse = getAccountNonce(from.getStorage().getAddress());
		
		if (nonceResponse.getErrType() == null) {
			return sendTransaction(from, transaction, nonceResponse.getResp());
		}
		
		return new BlocklinksResponse<String>(nonceResponse);
	}
	
	public BlocklinksResponse<String> sendTransaction(EthWallet from, EthTransaction transaction, BigInteger nonce) throws WalletLockedException {
		byte[] rawEncoded = transaction.signWithWallet(from, nonce);
		
		return performBlockingRpcAction(new RpcAction<String>() {

			@Override
			public String call() {
				return rpc.sendRawTransaction(rawEncoded);
			}
		}); 
	}
	
	public void sendTransaction(EthWallet from, EthTransaction transaction, BlocklinksCallable<String> callable) {
		String address = from.getStorage().getAddress();
		getAccountNonce(address, new BlocklinksCallable<BigInteger>() {
			
			@Override
			public void call(BlocklinksResponse<BigInteger> response) {
				if (response.getErrType() == null) {
					sendTransaction(from, transaction, response.getResp(), callable);
				} else {
					callable.call(new BlocklinksResponse<>(response));
				}
			}
		});
	}
	
	public void sendTransaction(EthWallet from, EthTransaction transaction, BigInteger nonce, BlocklinksCallable<String> callable) {		
		try {
			byte[] rawEncoded = transaction.signWithWallet(from, nonce);
			
			performAsyncRpcAction(new RpcAction<String>() {

				@Override
				public String call() {
					return rpc.sendRawTransaction(rawEncoded);
				}
				
			}, callable);
			
		} catch (WalletLockedException e) {
			callable.call(new BlocklinksResponse<String>(ErrorType.BAD_STATE, e));
			e.printStackTrace();
		}
	}

	public void listenForTxReceipt(final String txHash, final BlocklinksCallable<TransactionReceipt> callable) {
		listenForTxReceipt(txHash, receiptCheckIntervalMillis, receiptMaxChecks, callable);
	}
	
	private void listenForTxReceipt(final String txHash, final int checkIntervalMillis, final int checks,
			final BlocklinksCallable<TransactionReceipt> callable) {

		performAsyncRpcAction(new RpcAction<TransactionReceipt>() {

			@Override
			public TransactionReceipt call() {
				TransactionReceipt receipt = rpc.getTransactionReceipt(txHash);
				return receipt;
			}
		}, new BlocklinksCallable<TransactionReceipt>() {

			@Override
			public void call(BlocklinksResponse<TransactionReceipt> response) {
				if (response.getErrType() != null) {
					callable.call(response);
				} else {
					TransactionReceipt receipt = response.getResp();
					
					if (receipt != null && receipt.getBlockNumber() != null) {
						callable.call(response);
					} else if (checks <= 0) {
						callable.call(new BlocklinksResponse<TransactionReceipt>(
								ErrorType.OPERATION_TIMEOUT, new TxReceiptTimeoutException()));
					} else {
						synchronized (executor) {
							executor.schedule( new Runnable() {
								
								@Override
								public void run() {
									listenForTxReceipt(txHash, checkIntervalMillis, checks - 1, callable);
								}
							}, checkIntervalMillis, TimeUnit.MILLISECONDS);
						}
					}
				}
			}
		});
	}

}
