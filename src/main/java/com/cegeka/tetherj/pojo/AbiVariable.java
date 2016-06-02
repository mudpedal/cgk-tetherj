package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class AbiVariable implements Serializable {

    private static final long serialVersionUID = -5713661826032273529L;

    boolean indexed;
    String name;
    String type;
}
