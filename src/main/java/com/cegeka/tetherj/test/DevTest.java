package com.cegeka.tetherj.test;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cegeka.tetherj.*;
import com.cegeka.tetherj.api.EthereumService;
import com.cegeka.tetherj.api.TetherjHandle;
import com.cegeka.tetherj.api.TetherjResponse;
import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.FilterLogObject;
import com.cegeka.tetherj.pojo.FilterLogRequest;

public class DevTest {

    public static EthRpcClient c = new EthRpcClient();

    /**
     * MAIN TEST.
     * @param args process args
     */
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

                FilterLogRequest request = theDao.getEventFilter("Voted", 84);
                request.setFromBlock(BigInteger.valueOf(1550000));
                request.setToBlock("latest");

                AtomicBoolean finished = new AtomicBoolean(false);

                service.getEvents(request, new TetherjHandle<List<EthEvent>>() {
                    @Override
                    public void call(TetherjResponse<List<EthEvent>> response) {
                        if (response.isSuccessful()) {
                            for (EthEvent event : response.getValue()) {
                                System.out.println(Arrays.toString(event.getData()));
                            }
                        } else {
                            System.err.println(response.getException().getMessage());
                        }

                        finished.set(true);
                    }
                });

                while (!finished.get()) {
                    Future<TetherjResponse<BigInteger>> future = service.getLatestBlockNumberFuture();
                    TetherjResponse<BigInteger> response = future.get();
                    if (response.isSuccessful()) {
                        System.out.println("BALANCE IS " + response.getValue());
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
