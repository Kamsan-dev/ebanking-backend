package com.ebanking.app.exceptions;

public class BalanceNotSufficientException extends Throwable {
	
	public BalanceNotSufficientException(String message) {
		super(message);
	}

}
