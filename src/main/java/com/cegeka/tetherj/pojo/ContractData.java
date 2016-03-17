package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ContractData implements Serializable {
	
	private static final long serialVersionUID = -2719743504739845186L;

	String code;
	ContractInfo info;
}
