package com.cegeka.blocklinks.ethereum.pojo;

import java.io.Serializable;

public class ContractData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2719743504739845186L;
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ContractInfo getInfo() {
		return info;
	}

	public void setInfo(ContractInfo info) {
		this.info = info;
	}

	String code;
	ContractInfo info;
}
