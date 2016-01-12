package com.cegeka.test;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;

import com.cegeka.blocklinks.ethereum.EthRpcClient;
import com.cegeka.blocklinks.ethereum.Util;

public class DevTest {

	public static EthRpcClient c = new EthRpcClient();
	
	public static void main(String[] args) {
		c.unlockAccount(c.getCoinbase(), "secret");
		//System.out.println(c.getTransaction("0x19945f66caa2b830dac6e479114b10bce11637bace3e131c5c69db77dea0e561"));
		
		String[] acc = c.getAccounts();
		System.out.println(Arrays.toString(acc));
		
		String tx = c.sendTransaction(acc[0], acc[1], Util.fromEtherToWei(BigDecimal.ONE));
		
		if (tx != null) {
			System.out.println("Sent transaction " + tx);
		}
		
		System.out.println(c.getTransaction(tx));
		
		System.out.println(c.getTransactionReceipt("0x19945f66caa2b830dac6e479114b10bce11637bace3e131c5c69db77dea0e561"));
		
	}

}
