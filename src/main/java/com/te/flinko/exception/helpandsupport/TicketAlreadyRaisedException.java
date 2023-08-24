package com.te.flinko.exception.helpandsupport;

public class TicketAlreadyRaisedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TicketAlreadyRaisedException(String message) {
		super(message);
	}

}
