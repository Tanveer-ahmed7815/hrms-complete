package com.te.flinko.exception.account;

public class CustomExceptionForAccount extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CustomExceptionForAccount(String msg) {
		super(msg);
	}

}
