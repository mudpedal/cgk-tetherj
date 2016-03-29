package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserDoc implements Serializable {

    private static final long serialVersionUID = 7764692830010365466L;

    private UserDocMethods methods;
}
