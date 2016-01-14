package com.cegeka.blocklinks.ethereum.crypto;

import java.util.Formatter;

public class Util {
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
		return hexToBytes(hex.toCharArray());
	}

	public static byte[] hexToBytes(char[] hex) {
		if (hex.length % 2 != 0)
			throw new IllegalArgumentException("Must pass an even number of characters.");

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
	
	public static String[] generateECDSAPair() {
		String [] pair = new String[2];
		
		pair[0] = "7737e42d5ad971c955249a2ca14b35053f11edac828ebdb00c2a2b534ff7d168"; // insert private here
		pair[1] = "cae1660eb20b524ae6c5c0e7f741398be223d69c"; // insert public here
		
		return pair;
	}
}
