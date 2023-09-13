package com.ebanking.app;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ebanking.app.entites.AccountOperation;
import com.ebanking.app.entites.CurrentAccount;
import com.ebanking.app.entites.Customer;
import com.ebanking.app.entites.SavingAccount;
import com.ebanking.app.enums.AccountStatus;
import com.ebanking.app.enums.OperationType;
import com.ebanking.app.repositories.AccountOperationRepository;
import com.ebanking.app.repositories.BankAccountRepository;
import com.ebanking.app.repositories.CustomerRepository;

@SpringBootApplication
public class EbankingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankingBackendApplication.class, args);
	}
	
	@Bean
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
