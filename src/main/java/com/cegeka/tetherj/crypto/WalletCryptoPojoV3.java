package com.cegeka.tetherj.crypto;

import java.io.Serializable;

import lombok.Data;

/**
 * Pojo to store wallet crypto
 * 
 * @author Andrei Grigoriu
 *
 */
@Data
public class WalletCryptoPojoV3 implements Serializable {

	private static final long serialVersionUID = 3895065516835152955L;

	/**
	 * Internal pojo for iv
	 */
	@Data
	public class CipherParams implements Serializable {

		private static final long serialVersionUID = -5809750321955639194L;

		String iv;
	}

	/**
	 * Internal pojo for KDF params
	 *
	 */
	@Data
	public class KdfParams implements Serializable {

		private static final long serialVersionUID = 4851325137367204904L;

		String prf;
		int c;
		String salt;
		int dklen;
	}

	String cipher;
	String ciphertext;
	CipherParams cipherparams;
	String kdf;
	KdfParams kdfparams;
	String mac;

	public WalletCryptoPojoV3() {
		cipherparams = new CipherParams();
		kdfparams = new KdfParams();
	}
}
