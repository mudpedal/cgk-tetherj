package com.cegeka.blocklinks.ethereum.pojo;

public class ContractInfo {
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguageVersion() {
		return languageVersion;
	}

	public void setLanguageVersion(String languageVersion) {
		this.languageVersion = languageVersion;
	}

	public String getCompilerVersion() {
		return compilerVersion;
	}

	public void setCompilerVersion(String compilerVersion) {
		this.compilerVersion = compilerVersion;
	}

	public ContractAbiMethod[] getAbiDefinition() {
		return abiDefinition;
	}

	public void setAbiDefinition(ContractAbiMethod[] abiDefinition) {
		this.abiDefinition = abiDefinition;
	}

	String language;
	String languageVersion;
	String compilerVersion;
	ContractAbiMethod[] abiDefinition;
}
