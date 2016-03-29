package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class DevDocMethod implements Serializable {

    private static final long serialVersionUID = -6621194127186336761L;

    String details;
    DevDocMethodParams params;
}
