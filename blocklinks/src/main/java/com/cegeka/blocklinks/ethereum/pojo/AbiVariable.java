package com.cegeka.blocklinks.ethereum.pojo;

import java.io.Serializable;

public class AbiVariable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5713661826032273529L;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	String name;
	String type;
}
