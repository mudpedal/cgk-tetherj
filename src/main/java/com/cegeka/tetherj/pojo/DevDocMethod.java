package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevDocMethod implements Serializable {

    private static final long serialVersionUID = -6621194127186336761L;

    @JsonProperty("return")
    String returnValue;

    String details;
    DevDocMethodParams params;
}
