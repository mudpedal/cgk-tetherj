package com.cegeka.blocklinks.ethereum.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Formatter;

import org.bouncycastle.crypto.digests.KeccakDigest;

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
	
	public static KeyPair generateECDSAPair() {
		
		try {
			if (Security.getProvider("BC") == null) {
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			}
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
			keyGen.initialize(ecSpec);
		    return keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		
	    return null;
	}
	
	public static String getEthereumAddress(byte[] publicKey) {
		// generate MAC as per ethereum standard
		KeccakDigest md = new KeccakDigest(256);
		md.update(publicKey, 0, publicKey.length);
		byte[] digest = new byte[md.getDigestSize()];
		md.doFinal(digest, 0);
		
		byte[] trim = Arrays.copyOfRange(digest, 12, 32);
		
		return byteToHex(trim);
	}
}
