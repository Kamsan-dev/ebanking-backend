package com.ebanking.app.dtos;

import java.util.List;

import lombok.Data;

@Data
public class AccountHistoryDTO {
	
	private String accountId;
	private String owner;
	private double balance;
	private List<AccountOperationDTO> accountOperationDTOS;
	private int currentPage;
	private int totalPages;
	private int pageSize;

}
