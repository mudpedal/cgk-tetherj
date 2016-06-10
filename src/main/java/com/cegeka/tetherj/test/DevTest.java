package com.cegeka.tetherj.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.cegeka.tetherj.EthCall;
import com.cegeka.tetherj.EthRpcClient;
import com.cegeka.tetherj.EthSmartContract;
import com.cegeka.tetherj.EthSmartContractFactory;
import com.cegeka.tetherj.api.EthereumService;
import com.cegeka.tetherj.api.TetherjResponse;
import com.cegeka.tetherj.pojo.CompileOutput;

public class DevTest {

    public static EthRpcClient c = new EthRpcClient();

    public static void main(String[] args) {

        EthereumService service = new EthereumService(2);

        try {
            String sourceFilePath = "/home/andreicg/untouched/DAO/FULL.sol";
            String source = new String(Files.readAllBytes(Paths.get(sourceFilePath)), "UTF-8");
            TetherjResponse<CompileOutput> compileResponse = service.compileSolidity(source);

            if (compileResponse.isSuccessful()) {
                EthSmartContractFactory daoFactory = new EthSmartContractFactory(
                        compileResponse.getValue().getContractByName("DAO"));

                EthSmartContract theDao = daoFactory
                        .getContract("0xbb9bc244d798123fde783fcc1c72d3bb8c189413");

                EthCall call = theDao.callConstantMethod("balanceOf",
                        "e382dcaabf70dbce10dfcaecf0ac3b78184f6e65");
                TetherjResponse<Object[]> balanceResponse = service.makeCall(call);
                if (balanceResponse.isSuccessful()) {
                    System.out.println(Arrays.toString(balanceResponse.getValue()));
                }


            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
