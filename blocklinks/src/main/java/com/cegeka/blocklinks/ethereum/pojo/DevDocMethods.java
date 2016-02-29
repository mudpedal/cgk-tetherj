package com.cegeka.blocklinks.ethereum.pojo;

import java.io.Serializable;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class DevDocMethods implements Serializable {
	
	private static final long serialVersionUID = 6556283911516754341L;
	
	private HashMap<String, DevDocMethod> methods;

	public DevDocMethods() {
		methods = new HashMap<>();
	}

	@JsonAnySetter
	public void setDevDocMethod(String name, DevDocMethod method) {
		methods.put(name, method);
	}

	@JsonAnyGetter
	public HashMap<String, DevDocMethod> getMethods() {
		return methods;
	}
}
