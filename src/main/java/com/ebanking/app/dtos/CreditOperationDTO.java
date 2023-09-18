package com.ebanking.app.dtos;

import lombok.Data;

@Data
public class CreditOperationDTO {

	private String coveredBy;
	private String description;
	private double amount;
}
