package com.cegeka.tetherj.api;

/**
 * Returned after a blocklinks operation finishes.
 * 
 * @author Andrei Grigoriu
 *
 * @param <T>
 *            type of the valued returned.
 */
public class BlocklinksResponse<T> {
	/**
	 * Blocklinks defined ErrorType that has occured
	 * 
	 * @return null if no error, check for this first.
	 */
	public ErrorType getErrorType() {
		return errorType;
	}

	/**
	 * Returns exception that triggered the error. Sometimes its best to check
	 * cause as well.
	 * 
	 * @return null if no error
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Returns the actual payload, null if error occured.
	 * @return null if error occured. May be null if operation returns null.
	 */
	public T getValue() {
		return value;
	}

	private final ErrorType errorType;
	private final Exception exception;
	private final T value;

	/**
	 * 
	 * @param errorType
	 * @param exception
	 * @param resp
	 */
	public BlocklinksResponse(ErrorType errorType, Exception exception, T resp) {
		this.errorType = errorType;
		this.exception = exception;
		this.value = resp;
	}

	/**
	 * Create error response (value is set to null)
	 * @param errorType
	 * @param exception
	 */
	public BlocklinksResponse(ErrorType errorType, Exception exception) {
		this.errorType = errorType;
		this.exception = exception;
		this.value = null;
	}

	/**
	 * Create error response from other response copying only errorType and exception.
	 * @param otherResponse
	 */
	public <V> BlocklinksResponse(BlocklinksResponse<V> otherResponse) {
		this.errorType = otherResponse.errorType;
		this.exception = otherResponse.exception;
		this.value = null;
	}

}
