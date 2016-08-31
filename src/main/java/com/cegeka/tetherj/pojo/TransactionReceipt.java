package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionReceipt implements Serializable {

    private static final long serialVersionUID = 3551140643531052025L;

    private String blockHash;
    private String blockNumber;
    private String contractAddress;
    private String cumulativeGasUsed;
    private String gasUsed;
    private Object[] logs;
    private String transactionHash;
    private String transactionIndex;
}
