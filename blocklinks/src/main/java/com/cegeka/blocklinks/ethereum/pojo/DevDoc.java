package com.cegeka.blocklinks.ethereum.pojo;

import java.io.Serializable;

public class DevDoc implements Serializable {
	
	private static final long serialVersionUID = 297037653570409555L;
	private DevDocMethods methods;
	
	public DevDocMethods getMethods() {
		return methods;
	}
	public void setMethods(DevDocMethods methods) {
		this.methods = methods;
	}
}
