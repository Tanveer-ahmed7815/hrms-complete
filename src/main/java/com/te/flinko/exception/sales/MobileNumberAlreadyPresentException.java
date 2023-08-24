package com.te.flinko.exception.sales;

public class MobileNumberAlreadyPresentException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MobileNumberAlreadyPresentException() {
		super();
	}

	public MobileNumberAlreadyPresentException(String message) {
		super(message);
	}
	
	

}
