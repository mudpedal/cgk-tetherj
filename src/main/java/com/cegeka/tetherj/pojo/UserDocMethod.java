package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDocMethod implements Serializable {

    private static final long serialVersionUID = -6808833294590633503L;

    String notice;
}
