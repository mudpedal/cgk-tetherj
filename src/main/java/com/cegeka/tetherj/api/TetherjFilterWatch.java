package com.cegeka.tetherj.api;

import com.cegeka.tetherj.EthEvent;
import lombok.Data;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
@Data
public class TetherjFilterWatch {

    private TetherjHandle<List<EthEvent>> watchHandle;
    private AtomicBoolean isCancelled = new AtomicBoolean(false);

    public TetherjFilterWatch(TetherjHandle<List<EthEvent>> watchHandle) {
        this.watchHandle = watchHandle;
    }

    /**
     * Cancels watch of filter. Watch callback will stop being called.
     */
    public void cancel() {
        isCancelled.set(true);
    }
}
