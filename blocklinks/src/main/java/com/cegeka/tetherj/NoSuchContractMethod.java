package com.cegeka.tetherj;

/**
 * Thrown if contract method does not exist for contract.
 * 
 * @author Andrei Grigoriu
 *
 */
public class NoSuchContractMethod extends Exception {

	public NoSuchContractMethod(String string) {
		super(string);
	}

	private static final long serialVersionUID = 5600813193863948732L;

}
