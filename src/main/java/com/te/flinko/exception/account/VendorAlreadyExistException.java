package com.te.flinko.exception.account;

public class VendorAlreadyExistException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VendorAlreadyExistException(String message) {
		super(message);
	}

}
