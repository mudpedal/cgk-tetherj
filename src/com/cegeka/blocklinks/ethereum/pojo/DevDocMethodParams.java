package com.cegeka.blocklinks.ethereum.pojo;

import java.io.Serializable;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class DevDocMethodParams implements Serializable{
	
	private static final long serialVersionUID = -3148732962254935072L;
	
	private HashMap<String, String> params;

	public DevDocMethodParams() {
		params = new HashMap<>();
	}

	@JsonAnySetter
	public void setDevDocMethodParam(String name, String param) {
		params.put(name, param);
	}

	@JsonAnyGetter
	public HashMap<String, String> getParams() {
		return params;
	}
}
