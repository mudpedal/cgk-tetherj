package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterLogRequest implements Serializable {

    private static final long serialVersionUID = 4840490358731008368L;
    public static final FilterLogRequest DEFAULT = new FilterLogRequest();

    String fromBlock;
    String toBlock;
    String address;
    String[] topics;
}
