package com.te.flinko.exception.helpandsupport;

public class WrongAttachmentFileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WrongAttachmentFileException(String message) {
		super(message);
	}

}
