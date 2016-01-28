package com.cegeka.blocklinks.ethereum.pojo;

public class ContractData {
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
