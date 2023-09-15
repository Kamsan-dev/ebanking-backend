package com.ebanking.app.services;

import java.util.List;

import com.ebanking.app.dtos.AccountHistoryDTO;
import com.ebanking.app.dtos.AccountOperationDTO;
import com.ebanking.app.dtos.BankAccountDTO;
import com.ebanking.app.dtos.CurrentAccountDTO;
import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.dtos.SavingAccountDTO;
import com.ebanking.app.exceptions.BalanceNotSufficientException;
import com.ebanking.app.exceptions.BankAccountNotFoundException;

public interface BankAccountService {
	
	
	CustomerDTO saveCustomer(CustomerDTO customerDTO);
	CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long id);
	SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long id);
	List<CustomerDTO> listCustomers();
	BankAccountDTO getBankAccount(String accountId);
	void debit(String accountId, double amount, String description) throws BalanceNotSufficientException, BankAccountNotFoundException;
	void credit(String accountId, double amount, String description);
	void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;
	List<BankAccountDTO> bankAccountList();
	CustomerDTO getCustomer(Long id);
	void deleteCustomer(Long customerId);
	CustomerDTO updateCustomer(CustomerDTO customerDTO);
	List<AccountOperationDTO> accountHistory(String id);
	AccountHistoryDTO getAccountHistory(String accountId, int page, int size);
}
