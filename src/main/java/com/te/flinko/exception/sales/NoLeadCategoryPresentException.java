package com.te.flinko.exception.sales;

public class NoLeadCategoryPresentException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoLeadCategoryPresentException() {
		super();
		
	}

	public NoLeadCategoryPresentException(String message) {
		super(message);
		
	}

	
	
}
