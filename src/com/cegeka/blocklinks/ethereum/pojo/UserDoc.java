package com.cegeka.blocklinks.ethereum.pojo;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class UserDoc {
	private HashMap<String, UserDocMethod> methods;
	
	public UserDoc() {
		methods = new HashMap<>();
	}
	
	@JsonAnySetter
    public void setUserDocMethod(String name, UserDocMethod method) {
		methods.put(name, method);
    }
	
	@JsonAnyGetter
	public HashMap<String, UserDocMethod> getMethods() {
		return methods;
	}
}
