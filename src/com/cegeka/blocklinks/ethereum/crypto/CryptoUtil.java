package com.cegeka.blocklinks.ethereum.crypto;

import java.math.BigInteger;
import java.util.Formatter;

public class CryptoUtil {

	public static String byteToHexWithPrefix(final byte[] hash) {
		return "0x" + byteToHex(hash);
	}

	public static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static byte[] hexToBytes(String hex) {
		if (hex.startsWith("0x")) {
			hex = hex.substring(2);
		}
		return hexToBytes(hex.toCharArray());
	}

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
