package com.cegeka.blocklinks.api;

public class BlocklinksResponse<T> {
	public ErrorType getErrorType() {
		return errorType;
	}

	public Exception getException() {
		return exception;
	}

	public T getValue() {
		return value;
	}

	private final ErrorType errorType;
	private final Exception exception;
	private final T value;

	public BlocklinksResponse(ErrorType errorType, Exception exception, T resp) {
		this.errorType = errorType;
		this.exception = exception;
		this.value = resp;
	}

	public BlocklinksResponse(ErrorType errorType, Exception exception) {
		this.errorType = errorType;
		this.exception = exception;
		this.value = null;
	}

	public <V> BlocklinksResponse(BlocklinksResponse<V> otherResponse) {
		this.errorType = otherResponse.errorType;
		this.exception = otherResponse.exception;
		this.value = null;
	}

}
