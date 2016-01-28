package com.cegeka.blocklinks.ethereum.pojo;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class DevDoc {
	private HashMap<String, DevDocMethod> methods;

	public DevDoc() {
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
