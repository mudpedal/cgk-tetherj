package com.cegeka.tetherj.test;

import com.cegeka.tetherj.EthRpcClient;
import com.cegeka.tetherj.api.EthereumService;

public class DevTest {

    public static EthRpcClient c = new EthRpcClient();

    public static void main(String[] args) {

        EthereumService service = new EthereumService(2);
    }
}
