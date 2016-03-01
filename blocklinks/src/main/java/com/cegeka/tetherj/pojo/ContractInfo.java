package com.cegeka.tetherj.pojo;

import java.io.Serializable;

public class ContractInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9058606692051266438L;
	
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

	public UserDoc getUserDoc() {
		return userDoc;
	}

	public void setUserDoc(UserDoc userDoc) {
		this.userDoc = userDoc;
	}

	public DevDoc getDeveloperDoc() {
		return developerDoc;
	}

	public void setDeveloperDoc(DevDoc developerDoc) {
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
	UserDoc userDoc;
	DevDoc developerDoc;
}
