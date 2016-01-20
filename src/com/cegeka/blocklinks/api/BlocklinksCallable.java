package com.cegeka.blocklinks.api;

@FunctionalInterface
public interface BlocklinksCallable<T> {
	public void call(BlocklinksResponse<T> response);
}
