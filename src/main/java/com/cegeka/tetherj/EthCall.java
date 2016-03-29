package com.cegeka.tetherj;

import java.math.BigInteger;

import com.cegeka.tetherj.pojo.TransactionCall;

/**
 * Call or DryCall container for function executions.
 * 
 * @author Andrei Grigoriu
 *
 */
public class EthCall {

    private TransactionCall call;

    public EthCall(TransactionCall call) {
        this.call = call;
    }

    /**
     * @return Returns call object.
     */
    public TransactionCall getCall() {
        return call;
    }

    /**
     * Set a new gas limit for this call (to simulate gas).
     * 
     * @param gasLimit
     *            Gas limit value to set.
     */
    public void setGasLimit(BigInteger gasLimit) {
        call.setGas("0x" + gasLimit.toString(16));
    }

    /**
     * Set a new from address, to simulate privileges (usually).
     * 
     * @param from
     *            Ethereum from address to set.
     */
    public void setFrom(String from) {
        call.setFrom(from);
    }

    /**
     * Set a new from address, to simulate privileges (usually)/
     * 
     * @param from
     *            Ethereum from address to set, retrieved from wallet.
     */
    public void setFrom(EthWallet from) {
        call.setFrom(from.getAddress());
    }

    /**
     * Decode output received from ethereum client.
     * 
     * @param output
     *            Output data received from ethereum client.
     * @return Output data decoded into an array of Objects, the objects can be casted to known java
     *         types such BigInteger for quantity types, String for string etc.
     */
    public Object[] decodeOutput(String output) {
        return call.decodeOutput(output);
    }
}
