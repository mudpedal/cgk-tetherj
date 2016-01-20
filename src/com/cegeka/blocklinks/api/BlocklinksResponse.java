package com.cegeka.blocklinks.api;

public class BlocklinksResponse<T> {
	public ErrorTypes getErrType() {
		return errType;
	}

	public Exception getEx() {
		return ex;
	}

	public T getResp() {
		return resp;
	}

	private final ErrorTypes errType;
	private final Exception ex;
	private final T resp;
	
	public BlocklinksResponse (ErrorTypes errType, Exception ex, T resp) {
		this.errType = errType;
		this.ex = ex;
		this.resp = resp;
	}
}
