package com.cegeka.tetherj.pojo;

import lombok.Data;

@Data
public class FilterLogObject {
    private String type;
    private String logIndex;
    private String transactionIndex;
    private String transactionHash;
    private String blockHash;
    private String blockNumber;
    private String address;
    private String data;
    private String[] topics;
}
