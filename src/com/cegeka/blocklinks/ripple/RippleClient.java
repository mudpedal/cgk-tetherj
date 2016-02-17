package com.cegeka.blocklinks.ripple;

import com.ripple.client.BaseRippleClient;
import com.ripple.client.rpc.model.AccountInfo;
import com.ripple.client.rpc.model.AccountInfoRequest;
import com.ripple.client.rpc.model.AccountTxInfo;
import com.ripple.client.rpc.model.AccountTxRequest;
import com.ripple.client.rpc.model.CurrentLedgerInfo;
import com.ripple.client.rpc.model.ServerInfo;
import com.ripple.client.rpc.model.Transaction;
import com.ripple.client.rpc.model.TxAddress;

/**
 * The main entrypoint to use ripple via RPC
 * 
 * @author Cristian Sandu
 *
 */
public class RippleClient extends BaseRippleClient {
	public RippleClient() {
		super();
	}
	
	public RippleClient(String hostname, int port) {
		super(hostname, port);
	}
	
	public CurrentLedgerInfo getCurrentLedger() {
		return this.rippleServiceProxy.ledger_current();
	}
	
	public AccountInfo getAccountInfo(String account) {
		return this.rippleServiceProxy.account_info(new AccountInfoRequest(account));
	}
	
	public AccountInfo getRootAccountInfo() {
		return this.getAccountInfo(rootAccount);
	}
	
	public ServerInfo getServerInfo() {
		return this.rippleServiceProxy.server_info();
	}

	public AccountTxInfo getRootAccountTxs() {
		return this.getAccountTxs(rootAccount);
	}
	
	public AccountTxInfo getAccountTxs(String account) {
		return this.rippleServiceProxy.account_tx(new AccountTxRequest(account));
	}
	
	public Transaction getTransactionInfo(TxAddress transaction) {
		return this.rippleServiceProxy.tx(transaction);
	}
}
