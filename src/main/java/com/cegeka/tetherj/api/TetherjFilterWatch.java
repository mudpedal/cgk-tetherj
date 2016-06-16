package com.cegeka.tetherj.api;

import lombok.Data;

/**
 *
 */
@Data
public class TetherjFilterWatch {

    private TetherjHandle<Object[]> watchHandle;

    public TetherjFilterWatch(TetherjHandle<Object[]> watchHandle) {
        this.watchHandle = watchHandle;
    }

    /**
     * Cancels watch of filter. Watch callback will stop being called.
     */
    public void cancel() {

    }
}
