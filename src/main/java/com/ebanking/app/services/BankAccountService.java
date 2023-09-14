package com.ebanking.app.services;

import java.util.List;

import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.entites.BankAccount;
import com.ebanking.app.entites.CurrentAccount;
import com.ebanking.app.entites.Customer;
import com.ebanking.app.entites.SavingAccount;
import com.ebanking.app.exceptions.BalanceNotSufficientException;
import com.ebanking.app.exceptions.BankAccountNotFoundException;

public interface BankAccountService {
	
	
	Customer saveCustomer(Customer customer);
	CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long id);
	SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long id);
	List<CustomerDTO> listCustomers();
	BankAccount getBankAccount(String accountId);
	void debit(String accountId, double amount, String description) throws BalanceNotSufficientException, BankAccountNotFoundException;
	void credit(String accountId, double amount, String description);
	void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;
	List<BankAccount> bankAccountList();
	CustomerDTO getCustomer(Long id);
}
