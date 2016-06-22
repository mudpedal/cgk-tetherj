package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractData implements Serializable {

    private static final long serialVersionUID = -2719743504739845186L;

    String code;
    ContractInfo info;
}
