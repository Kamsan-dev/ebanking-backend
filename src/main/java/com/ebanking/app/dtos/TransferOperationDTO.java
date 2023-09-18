package com.ebanking.app.dtos;

import lombok.Data;

@Data
public class TransferOperationDTO {
	
	private String accountIdDestination;
	private double amount;
	private String description;

}
