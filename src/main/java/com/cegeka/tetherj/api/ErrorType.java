package com.cegeka.tetherj.api;

/**
 * Tetherj error types.
 * 
 * @author Andrei Grigoriu
 *
 */
public enum ErrorType {
    /* !< blockchain client is not responding correctly or offline */
    BLOCKCHAIN_CLIENT_BAD_CONNECTION,

    /* !< blockchain client returned an error */
    BLOCKCHAIN_CLIENT_OPERATION_ERROR,

    UNKNOWN_ERROR, /* !< unknown error */
    BAD_STATE, /* !< called in a wrong manner */
    OPERATION_TIMEOUT /* !< timeout */
}
