package com.te.flinko.exception;

public class PaymentFailedException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PaymentFailedException(String msg) {
		super(msg);
	}

}
