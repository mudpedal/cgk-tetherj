package com.cegeka.tetherj.api;

/**
 * Returned after a tetherj operation finishes.
 *
 * @author Andrei Grigoriu
 *
 * @param <T>
 *            type of the valued returned.
 */
public class TetherjResponse<T> {
    /**
     * Tetherj defined ErrorType that has occured
     *
     * @return null if no error, check for this first.
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    public boolean isSuccessful() {
        return getErrorType() == null;
    }

    /**
     * Returns exception that triggered the error. Sometimes its best to check cause as well.
     *
     * @return null if no error
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns the actual payload, null if error occured.
     *
     * @return null if error occured. May be null if operation returns null.
     */
    public T getValue() {
        return value;
    }

    private final ErrorType errorType;
    private final Exception exception;
    private final T value;

    public static <T> TetherjResponse<T> success(T resp) {
        return new TetherjResponse<>(null, null, resp);
    }

    public static <V,T> TetherjResponse<T> failure(TetherjResponse<V> failureResponse) {
        return new TetherjResponse<>(failureResponse);
    }

    /**
     * Construct response.
     * @param errorType Error type to set.
     * @param exception Exception to set.
     * @param resp Value response to set.
     */
    public TetherjResponse(ErrorType errorType, Exception exception, T resp) {
        this.errorType = errorType;
        this.exception = exception;
        this.value = resp;
    }

    /**
     * Create error response (value is set to null).
     *
     * @param errorType Error Type to set.
     * @param exception Error Type to set.
     */
    public TetherjResponse(ErrorType errorType, Exception exception) {
        this.errorType = errorType;
        this.exception = exception;
        this.value = null;
    }

    /**
     * Create error response from other response copying only errorType and exception.
     *
     * @param otherResponse Other error response.
     */
    public <V> TetherjResponse(TetherjResponse<V> otherResponse) {
        this.errorType = otherResponse.errorType;
        this.exception = otherResponse.exception;
        this.value = null;
    }

}
