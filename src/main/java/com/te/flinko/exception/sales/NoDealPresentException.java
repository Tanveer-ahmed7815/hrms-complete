package com.te.flinko.exception.sales;

public class NoDealPresentException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoDealPresentException() {
		super();
	}

	public NoDealPresentException(String message) {
		super(message);
	}

	
}
