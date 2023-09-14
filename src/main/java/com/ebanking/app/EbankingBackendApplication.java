package com.ebanking.app;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
import com.ebanking.app.repositories.AccountOperationRepository;
import com.ebanking.app.repositories.BankAccountRepository;
import com.ebanking.app.repositories.CustomerRepository;
import com.ebanking.app.services.BankAccountService;

@SpringBootApplication
public class EbankingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankingBackendApplication.class, args);
	}
	
	
	@Bean
	CommandLineRunner clr(BankAccountService bankAccountService) {
		
		return args -> {
			Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customer);
			});
			
			bankAccountService.listCustomers().forEach(customer -> {
				
				try {
					bankAccountService
					.saveCurrentBankAccount(Math.random() * 9000, 5000, customer.getId());
				bankAccountService
				.saveSavingBankAccount(Math.random() * 15000, 5.5, customer.getId());
				
				List<BankAccount> listAccount = bankAccountService.bankAccountList();
				
				for (BankAccount ba : listAccount) {
					for (int i = 0; i < 10; i++) {
							bankAccountService.credit(ba.getId(), 10000 + Math.random() * 12000, "Credit");
							bankAccountService.debit(ba.getId(), 10000 + 1000 + Math.random() * 9000, "Debit");
						}
					}
				} catch (CustomerNotFoundException | BankAccountNotFoundException | BalanceNotSufficientException e) {
					e.printStackTrace();
				}  
			});
		};
	}
	//@Bean
	CommandLineRunner start(CustomerRepository CR, 
			AccountOperationRepository AOR,
			BankAccountRepository BAR) {
		return args -> {
			
			Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				CR.save(customer);
			});
			
			CR.findAll().forEach(cust -> {
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random()*9000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverdraft(9000);
				currentAccount.setCurrency("€");
				BAR.save(currentAccount);
				
				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random()*9000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);
				savingAccount.setCurrency("€");
				BAR.save(savingAccount);
			});
			
			BAR.findAll().forEach(bankAccount -> {
				for (int i = 0; i < 10; i++) {
					AccountOperation ao = new AccountOperation();
					ao.setBankAccount(bankAccount);
					ao.setOperationDate(new Date());
					ao.setType(Math.random()> 0.5 ? 
							OperationType.DEBIT : OperationType.CREDIT);
					ao.setAmount(Math.random() * 12000);
					AOR.save(ao);
				}
			});
			
		};
	}

}
