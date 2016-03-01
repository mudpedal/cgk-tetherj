package com.cegeka.tetherj.crypto;

import java.io.Serializable;

/**
 * Pojo to store wallet crypto
 * 
 * @author Andrei Grigoriu
 *
 */
public class WalletCryptoPojoV3 implements Serializable {

	private static final long serialVersionUID = 3895065516835152955L;

	/**
	 * Internal pojo for iv
	 */
	public class CipherParams implements Serializable {

		private static final long serialVersionUID = -5809750321955639194L;

		@Override
		public String toString() {
			return "CipherParams [iv=" + iv + "]";
		}

		String iv;

		public String getIv() {
			return iv;
		}

		public void setIv(String iv) {
			this.iv = iv;
		}
	}

	/**
	 * Internal pojo for KDF params
	 *
	 */
	public class KdfParams implements Serializable {

		private static final long serialVersionUID = 4851325137367204904L;
		
		@Override
		public String toString() {
			return "KdfParams [prf=" + prf + ", c=" + c + ", salt=" + salt + ", dklen=" + dklen + "]";
		}

		public String getPrf() {
			return prf;
		}

		public void setPrf(String prf) {
			this.prf = prf;
		}

		public int getC() {
			return c;
		}

		public void setC(int c) {
			this.c = c;
		}

		public String getSalt() {
			return salt;
		}

		public void setSalt(String salt) {
			this.salt = salt;
		}

		public int getDklen() {
			return dklen;
		}

		public void setDklen(int dklen) {
			this.dklen = dklen;
		}

		String prf;
		int c;
		String salt;
		int dklen;
	}

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	public String getCiphertext() {
		return ciphertext;
	}

	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}

	public CipherParams getCipherparams() {
		return cipherparams;
	}

	public void setCipherparams(CipherParams cipherparams) {
		this.cipherparams = cipherparams;
	}

	public String getKdf() {
		return kdf;
	}

	public void setKdf(String kdf) {
		this.kdf = kdf;
	}

	public KdfParams getKdfparams() {
		return kdfparams;
	}

	public void setKdfparams(KdfParams kdfparams) {
		this.kdfparams = kdfparams;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	String cipher;
	String ciphertext;

	@Override
	public String toString() {
		return "WalletCrypto [cipher=" + cipher + ", ciphertext=" + ciphertext + ", cipherparams=" + cipherparams
				+ ", kdf=" + kdf + ", kdfparams=" + kdfparams + ", mac=" + mac + "]";
	}

	CipherParams cipherparams;
	String kdf;
	KdfParams kdfparams;
	String mac;

	public WalletCryptoPojoV3() {
		cipherparams = new CipherParams();
		kdfparams = new KdfParams();
	}
}
