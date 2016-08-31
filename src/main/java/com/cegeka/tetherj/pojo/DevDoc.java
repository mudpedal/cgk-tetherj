package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevDoc implements Serializable {

    private static final long serialVersionUID = 297037653570409555L;

    String title;
    private DevDocMethods methods;
}
