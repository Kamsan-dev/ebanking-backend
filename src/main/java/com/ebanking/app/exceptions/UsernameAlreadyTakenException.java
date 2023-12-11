package com.ebanking.app.exceptions;

public class UsernameAlreadyTakenException extends RuntimeException{
	
	public UsernameAlreadyTakenException(String message) {
		super(message);
	}
}