package com.ebanking.app.dtos;


import java.util.Date;

import com.ebanking.app.entites.BankAccount;
import com.ebanking.app.enums.OperationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class AccountOperationDTO {

	private Long id;
	private Date operationDate;
	private double amount;
	private OperationType type;
	private String description;

}
