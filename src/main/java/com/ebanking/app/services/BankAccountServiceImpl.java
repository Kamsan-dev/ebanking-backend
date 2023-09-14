package com.ebanking.app.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.entites.AccountOperation;
import com.ebanking.app.entites.BankAccount;
import com.ebanking.app.entites.CurrentAccount;
import com.ebanking.app.entites.Customer;
import com.ebanking.app.entites.SavingAccount;
import com.ebanking.app.enums.AccountStatus;
import com.ebanking.app.enums.OperationType;
import com.ebanking.app.exceptions.BalanceNotSufficientException;
import com.ebanking.app.exceptions.BankAccountNotFoundException;
import com.ebanking.app.exceptions.CustomerNotFoundException;
import com.ebanking.app.mappers.BankAccountMapperImpl;
import com.ebanking.app.repositories.AccountOperationRepository;
import com.ebanking.app.repositories.BankAccountRepository;
import com.ebanking.app.repositories.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j

public class BankAccountServiceImpl implements BankAccountService {
	
	private CustomerRepository customerRepository;
	private BankAccountRepository bankAccountRepository;
	private AccountOperationRepository accountOperationRepository;
	private BankAccountMapperImpl DTOMapper;

	@Override
	public Customer saveCustomer(Customer customer) {
		log.info("Saving customer");
		return customerRepository.save(customer);
	}

	@Override
	public List<CustomerDTO> listCustomers() {
		return customerRepository.findAll()
				.stream()
				.map(cust -> DTOMapper.fromCustomer(cust))
				.collect(Collectors.toList());
	}

	@Override
	public BankAccount getBankAccount(String accountId) {
		return bankAccountRepository.findById(accountId)
				.orElseThrow(() -> 
				new BankAccountNotFoundException("Bank account does not exist"));
	}

	@Override
	public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
		BankAccount bankAccount = this.getBankAccount(accountId);
		if (bankAccount.getBalance() < amount) {
			throw new BalanceNotSufficientException("Balance not sufficent");
		}
		
		AccountOperation accountOperation = new AccountOperation();
		accountOperation.setType(OperationType.DEBIT);
		accountOperation.setDescription(description);
		accountOperation.setBankAccount(bankAccount);
		accountOperation.setAmount(amount);
		accountOperation.setOperationDate(new Date());
		accountOperationRepository.save(accountOperation);
		
		bankAccount.setBalance(bankAccount.getBalance() - amount);
		bankAccountRepository.save(bankAccount);
		
	}

	@Override
	public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException{
		BankAccount bankAccount = this.getBankAccount(accountId);
		AccountOperation accountOperation = new AccountOperation();
		accountOperation.setType(OperationType.CREDIT);
		accountOperation.setDescription(description);
		accountOperation.setBankAccount(bankAccount);
		accountOperation.setAmount(amount);
		accountOperation.setOperationDate(new Date());
		accountOperationRepository.save(accountOperation);
		
		bankAccount.setBalance(bankAccount.getBalance() + amount);
		bankAccountRepository.save(bankAccount);
		
	}

	@Override
	public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
		this.debit(accountIdSource, amount, "Transfert to " + accountIdDestination);
		this.credit(accountIdDestination, amount, "Tranfert from " + accountIdSource);
		
	}

	@Override
	public CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long id) {
		Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("CustomerNotFound"));
		
		CurrentAccount currentAccount = new CurrentAccount();
		currentAccount.setId(UUID.randomUUID().toString());
		currentAccount.setCustomer(customer);
		currentAccount.setBalance(initialBalance);
		currentAccount.setStatus(AccountStatus.CREATED);
		currentAccount.setCreatedAt(new Date());
		currentAccount.setOverdraft(overDraft);
		
		return bankAccountRepository.save(currentAccount);
	}

	@Override
	public SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long id) {
		Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("CustomerNotFound"));
		
		SavingAccount currentAccount = new SavingAccount();
		currentAccount.setId(UUID.randomUUID().toString());
		currentAccount.setCustomer(customer);
		currentAccount.setBalance(initialBalance);
		currentAccount.setStatus(AccountStatus.CREATED);
		currentAccount.setCreatedAt(new Date());
		currentAccount.setInterestRate(interestRate);
		
		return bankAccountRepository.save(currentAccount);
	}
	
	@Override
	public List<BankAccount> bankAccountList() {
		return bankAccountRepository.findAll();
	}
	
	@Override
	public CustomerDTO getCustomer(Long id) {
		Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("CustomerNotFound"));
		return DTOMapper.fromCustomer(customer);
	}
}
