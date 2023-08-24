package com.te.flinko.exception.sales;

public class EmailAlreadyPresentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public EmailAlreadyPresentException() {
		super();
		
	}



	public EmailAlreadyPresentException(String message) {
		super(message);
		
	}

	
	
}
