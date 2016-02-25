package com.cegeka.blocklinks.ethereum.crypto;

import java.math.BigInteger;
import java.util.Formatter;

/**
 * Util for both crypto and decoding
 * 
 * @author Andrei Grigoriu
 *
 */
public class CryptoUtil {

	/**
	 * Convert bytes to hex + 0x in front
	 * 
	 * @param data
	 *            to convert
	 * @return hex string
	 */
	public static String byteToHexWithPrefix(final byte[] data) {
		return "0x" + byteToHex(data);
	}

	/**
	 * Convert bytes to hex without (no 0x in front)
	 * 
	 * @param data
	 *            to convert
	 * @return hex strng
	 */
	public static String byteToHex(final byte[] data) {
		Formatter formatter = new Formatter();
		for (byte b : data) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	/**
	 * Converts hex to bytes
	 * 
	 * @param hex
	 *            to decoded (may have 0x in front)
	 * @return byte data
	 */
	public static byte[] hexToBytes(String hex) {
		if (hex.startsWith("0x")) {
			hex = hex.substring(2);
		}
		return hexToBytes(hex.toCharArray());
	}

	/**
	 * Convert hex to BigInteger
	 * 
	 * @param hex
	 *            to convert
	 * @return biginteger from hex
	 */
	public static BigInteger hexToBigInteger(String hex) {
		if (hex == null) {
			return null;
		}

		if (hex.startsWith("0x")) {
			hex = hex.substring(2);
		}

		// remove starting zeros
		while (hex.length() > 1 && hex.charAt(0) == '0') {
			hex = hex.substring(1);
		}

		return new BigInteger(hex, 16);
	}

	/**
	 * Converts hex to bytes
	 * 
	 * @param hex
	 *            to decoded (may have 0x in front)
	 * @return byte data
	 */
	public static byte[] hexToBytes(char[] hex) {
		if (hex.length % 2 != 0) {
			throw new IllegalArgumentException("Must pass an even number of characters.");
		}

		int length = hex.length >> 1;
		byte[] raw = new byte[length];
		for (int o = 0, i = 0; o < length; o++) {
			raw[o] = (byte) ((getHexCharValue(hex[i++]) << 4) | getHexCharValue(hex[i++]));
		}
		return raw;
	}

	public static byte getHexCharValue(char c) {
		if (c >= '0' && c <= '9') {
			return (byte) (c - '0');
		}
		if (c >= 'A' && c <= 'F') {
			return (byte) (10 + c - 'A');
		}
		if (c >= 'a' && c <= 'f') {
			return (byte) (10 + c - 'a');
		}
		throw new IllegalArgumentException("Invalid hex character");
	}
}
