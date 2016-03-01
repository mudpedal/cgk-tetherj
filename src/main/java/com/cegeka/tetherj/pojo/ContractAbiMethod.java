package com.cegeka.tetherj.pojo;

import java.io.Serializable;

public class ContractAbiMethod implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5008090255193587747L;
	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbiVariable[] getInputs() {
		return inputs;
	}

	public void setInputs(AbiVariable[] inputs) {
		this.inputs = inputs;
	}

	public AbiVariable[] getOutputs() {
		return outputs;
	}

	public void setOutputs(AbiVariable[] outputs) {
		this.outputs = outputs;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	boolean constant;
	String name;
	AbiVariable[] inputs;
	AbiVariable[] outputs;
	String type;

}
