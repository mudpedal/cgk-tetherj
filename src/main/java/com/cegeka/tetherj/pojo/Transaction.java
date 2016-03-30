package com.cegeka.tetherj.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class Transaction implements Serializable {

    private static final long serialVersionUID = 5526521386363782072L;

    public String blockHash;
    public String blockNumber;
    public String from;
    public String gas;
    public String gasPrice;
    public String hash;
    public String input;
    public String nonce;
    public String to;
    public String transactionIndex;
    public String value;
}
