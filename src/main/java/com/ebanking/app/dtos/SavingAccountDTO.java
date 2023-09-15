package com.ebanking.app.dtos;

import java.util.Date;

import com.ebanking.app.enums.AccountStatus;

import lombok.Data;

@Data
public class SavingAccountDTO extends BankAccountDTO {
	
	
	private String id;
	private double balance;
	private Date createdAt;
	private AccountStatus status;
	private String currency;
	private CustomerDTO customerDTO;
	private double interestRate;
} 
