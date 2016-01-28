package com.cegeka.blocklinks.ethereum.pojo;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class CompileOutput {
	private HashMap<String, ContractData> contractData;
	
	public CompileOutput() {
		contractData = new HashMap<>();
	}
	
	@JsonAnySetter
    public void setDynamicContractData(String name, ContractData contract) {
		contractData.put(name, contract);
    }
	
	@JsonAnyGetter
	public HashMap<String, ContractData> getContractData() {
		return contractData;
	}
	
	public String[] getContractNames() {
		String[] names = new String[contractData.keySet().size()];
		contractData.keySet().toArray(names);
		return names;
	}
	
	public ContractData getContractByName(String name) {
		return contractData.get(name);
	}
}
