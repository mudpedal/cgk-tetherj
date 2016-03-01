package com.cegeka.tetherj.pojo;

import java.io.Serializable;

public class UserDoc implements Serializable {

	private static final long serialVersionUID = 7764692830010365466L;
	
	private UserDocMethods methods;

	public UserDocMethods getMethods() {
		return methods;
	}

	public void setMethods(UserDocMethods methods) {
		this.methods = methods;
	}
}
