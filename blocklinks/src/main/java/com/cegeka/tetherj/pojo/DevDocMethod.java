package com.cegeka.tetherj.pojo;

import java.io.Serializable;

public class DevDocMethod implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6621194127186336761L;
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public DevDocMethodParams getParams() {
		return params;
	}

	public void setParams(DevDocMethodParams params) {
		this.params = params;
	}

	String details;
	DevDocMethodParams params;
}
