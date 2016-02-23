package com.cegeka.blocklinks.ethereum;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Small web3 like util.
 * 
 * @author Andrei Grigoriu
 *
 */
public class Util {
	public static final BigDecimal etherValue = new BigDecimal(1000000000000000000L);

	/**
	 * Converts ether to wei.
	 * 
	 * @param ether
	 *            to convert
	 * @return wei
	 */
	public static BigInteger fromEtherToWei(int ether) {
		BigDecimal etherAsDecimal = BigDecimal.valueOf(ether);
		return fromEtherToWei(etherAsDecimal);
	}

	/**
	 * Converts ether to wei.
	 * 
	 * @param ether
	 *            to convert
	 * @return wei
	 */
	public static BigInteger fromEtherToWei(BigDecimal ether) {
		return ether.multiply(etherValue).toBigInteger();
	}

	/**
	 * Converts wei to ether.
	 * 
	 * @param wei
	 *            to convert
	 * @return ether
	 */
	public static BigDecimal fromWeiToEther(BigInteger wei) {
		BigDecimal weiDecimal = new BigDecimal(wei);
		return weiDecimal.divide(etherValue);
	}
}
