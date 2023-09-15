package com.ebanking.app.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ebanking.app.dtos.AccountHistoryDTO;
import com.ebanking.app.dtos.AccountOperationDTO;
import com.ebanking.app.dtos.BankAccountDTO;
import com.ebanking.app.services.BankAccountService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/")
public class BankAccountController {
	
	private BankAccountService bankAccountService;
	
	@GetMapping("/accounts")
	public List<BankAccountDTO> bankAccount(){
		log.info("fetch account");
		return bankAccountService.bankAccountList();
	}
	
	@GetMapping("/accounts/{accountId}")
	public BankAccountDTO getCustomer(@PathVariable(name="accountId") 
		String accountId){
		return bankAccountService.getBankAccount(accountId);
	}
	
	@GetMapping("/accounts/{accountId}/operations")
	public List<AccountOperationDTO> accountHistory(@PathVariable(name="accountId") 
		String accountId){
		return bankAccountService.accountHistory(accountId);
	}
	
	@GetMapping("/accounts/{accountId}/operationsHistory")
	public AccountHistoryDTO getAccountHistory(
			@PathVariable(name="accountId")String accountId,
			@RequestParam(name="page", defaultValue = "0") int page,
			@RequestParam(name="size", defaultValue = "5") int size){
		return bankAccountService.getAccountHistory(accountId, page, size);
	}

}
