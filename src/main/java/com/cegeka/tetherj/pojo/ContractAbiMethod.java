package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractAbiMethod implements Serializable {

    private static final long serialVersionUID = 5008090255193587747L;

    boolean anonymous;
    boolean constant;
    String name;
    AbiVariable[] inputs;
    AbiVariable[] outputs;
    String type;

}
