package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ContractAbiMethod implements Serializable {

    private static final long serialVersionUID = 5008090255193587747L;

    boolean constant;
    String name;
    AbiVariable[] inputs;
    AbiVariable[] outputs;
    String type;

}
