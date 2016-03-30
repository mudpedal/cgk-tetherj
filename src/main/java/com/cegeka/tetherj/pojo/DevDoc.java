package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class DevDoc implements Serializable {

    private static final long serialVersionUID = 297037653570409555L;

    private DevDocMethods methods;
}
