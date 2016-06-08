package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractInfo implements Serializable {

    private static final long serialVersionUID = -9058606692051266438L;

    String language;
    String languageVersion;
    String compilerVersion;
    String compilerOptions;
    ContractAbiMethod[] abiDefinition;
    String source;
    UserDoc userDoc;
    DevDoc developerDoc;
}
