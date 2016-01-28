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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Object getUserDoc() {
		return userDoc;
	}

	public void setUserDoc(Object userDoc) {
		this.userDoc = userDoc;
	}

	public Object getDeveloperDoc() {
		return developerDoc;
	}

	public void setDeveloperDoc(Object developerDoc) {
		this.developerDoc = developerDoc;
	}

	public String getCompilerOptions() {
		return compilerOptions;
	}

	public void setCompilerOptions(String compilerOptions) {
		this.compilerOptions = compilerOptions;
	}

	String language;
	String languageVersion;
	String compilerVersion;
	String compilerOptions;
	ContractAbiMethod[] abiDefinition;
	String source;
	Object userDoc;
	Object developerDoc;
}
