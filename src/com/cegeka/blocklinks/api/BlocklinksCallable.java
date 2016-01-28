package com.cegeka.blocklinks.api;

/**
 * Interface to assign as param to async methods. Will be called when the
 * desired operation ends with either success or fail.
 * 
 * @author Andrei Grigoriu
 *
 * @param <T> type of the valued returned.
 */
@FunctionalInterface
public interface BlocklinksCallable<T> {
	
	/**
	 * This method will be called when the async operation finishes.
	 * @param response is the result of the async operation.
	 */
	public void call(BlocklinksResponse<T> response);
}
