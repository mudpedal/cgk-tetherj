package com.cegeka.blocklinks.ethereum.crypto;

public class WalletCryptoPojoV3 {
	public class CipherParams {
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

	public class KdfParams {

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
