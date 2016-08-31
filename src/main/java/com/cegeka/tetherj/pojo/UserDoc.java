package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDoc implements Serializable {

    private static final long serialVersionUID = 7764692830010365466L;

    private UserDocMethods methods;
}
