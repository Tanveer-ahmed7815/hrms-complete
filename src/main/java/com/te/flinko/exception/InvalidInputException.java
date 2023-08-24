package com.te.flinko.exception;

public class InvalidInputException extends RuntimeException {

	String message;
	
public InvalidInputException(String message) {
	super(message);
}
}
