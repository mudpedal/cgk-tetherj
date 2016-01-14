package com.cegeka.blocklinks.ethereum;

import java.awt.image.ByteLookupTable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;

import com.cegeka.blocklinks.ethereum.WalletCrypto.CipherParams;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EthWallet {
	
	/* Getters Setters*/
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public WalletCrypto getCrypto() {
		return crypto;
	}

	public void setCrypto(WalletCrypto crypto) {
		this.crypto = crypto;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	String address;
	WalletCrypto crypto;
	String id;
	int version;
	
	private EthWallet() {
	}
	
	@Override
	public String toString() {
		return "EthWallet [address=" + address + ", crypto=" + crypto + ", id=" + id + ", version=" + version + "]";
	}

	public static EthWallet createWallet(String passphrase) {
		EthWallet wallet = new EthWallet();
		wallet.version = 3;
		wallet.id = UUID.randomUUID().toString();
		wallet.address = "cae1660eb20b524ae6c5c0e7f741398be223d69c";
		byte[] addressBytes = EthWallet.hexToBytes(wallet.address);
		String addressTranslated = EthWallet.byteToHex(addressBytes);
		
		WalletCrypto crypto = new WalletCrypto();
		wallet.crypto = crypto;
		crypto.cipher = "aes-128-ctr";
		try {
			
			Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
			String salt = "7431f7e1f1d253fdb0a74d597267fad836786863d70b451162011937279351ec";
			byte[] saltBytes = EthWallet.hexToBytes(salt);
			
			crypto.kdf = "pbkdf2";
			crypto.kdfparams.salt = salt;
			crypto.kdfparams.dklen = 32;
			crypto.kdfparams.prf = "hmac-sha256";
			crypto.kdfparams.c = 262144;
			
			
			try {
				byte[] key = PBKDF2.derive(passphrase, EthWallet.hexToBytes(salt), crypto.kdfparams.c, 32);
			
				String privateKey = "7737e42d5ad971c955249a2ca14b35053f11edac828ebdb00c2a2b534ff7d168";
				byte[] privKeyBytes = EthWallet.hexToBytes(privateKey);
				
				System.out.println("Key is " + EthWallet.byteToHex(key));
				byte[] trimmedKey = Arrays.copyOfRange(key, 0, 16);
				byte[] macKey = Arrays.copyOfRange(key, 16, 32);
				
				System.out.println("MAC KEY IS " + EthWallet.byteToHex(macKey));
				
				SecretKeySpec secretKeySpec = new SecretKeySpec(trimmedKey, "AES");
				c.init(Cipher.ENCRYPT_MODE, secretKeySpec);
				byte[] iv = c.getIV();
				
				//System.out.println("IV is " + new String(iv, "UTF-8"));
				byte[] ciphertext = c.doFinal(privKeyBytes);
				System.out.println("Cipher IS " + EthWallet.byteToHex(ciphertext));
				
				//System.out.println("cipher is " + new String(ciphertext, "UTF-8"));
				
				KeccakDigest md = new KeccakDigest(256); //same as DigestSHA3 md = new SHA3.Digest256();
				
				byte[] macSource = new byte[macKey.length + ciphertext.length];
				System.arraycopy(macKey, 0, macSource, 0, macKey.length);
				System.arraycopy(ciphertext, 0, macSource, macKey.length, ciphertext.length);

				md.update(macSource, 0, macSource.length);
			
				byte[] mac = new byte[md.getDigestSize()]; 
				md.doFinal(mac, 0);
				crypto.mac = EthWallet.byteToHex(mac);
				crypto.cipherparams.iv = EthWallet.byteToHex(iv);
				crypto.ciphertext = EthWallet.byteToHex(ciphertext);
				
				
				
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
			
			
			
		
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		
		return wallet;
	}
	
	public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
	
	public static byte[] hexToBytes(String hex)
	{
	    return hexToBytes(hex.toCharArray());
	}
	
	public static byte[] hexToBytes(char[] hex) {
	    if (hex.length % 2 != 0)
	        throw new IllegalArgumentException("Must pass an even number of characters.");

	    int length = hex.length >> 1;
	    byte[] raw = new byte[length];
	    for (int o = 0, i = 0; o < length; o++) {
	        raw[o] = (byte) ((getHexCharValue(hex[i++]) << 4)
	                        | getHexCharValue(hex[i++]));
	    }
	    return raw;
	}
	
	public static byte getHexCharValue(char c)
	{
	    if (c >= '0' && c <= '9')
	        return (byte) (c - '0');
	    if (c >= 'A' && c <= 'F')
	        return (byte) (10 + c - 'A');
	    if (c >= 'a' && c <= 'f')
	        return (byte) (10 + c - 'a');
	    throw new IllegalArgumentException("Invalid hex character");
	}
	
	public static EthWallet loadWalletFromString(String wallet) {
		return new EthWallet();
	}
	
	public static EthWallet loadWalletFromFile(File wallet) {
		return new EthWallet();
	}
	
	public void writeToFile(File file) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(this);
			//System.out.println(json);
			mapper.writeValue(file, this);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toJsonString() {
		return "";
	}
	
}
