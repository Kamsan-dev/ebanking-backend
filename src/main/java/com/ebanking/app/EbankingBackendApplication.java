package com.ebanking.app;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.ebanking.app.dtos.BankAccountDTO;
import com.ebanking.app.dtos.CreditOperationDTO;
import com.ebanking.app.dtos.CurrentAccountDTO;
import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.dtos.DebitOperationDTO;
import com.ebanking.app.dtos.SavingAccountDTO;
import com.ebanking.app.entites.AccountOperation;
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
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}
	
	
	@Bean
	CommandLineRunner clr(BankAccountService bankAccountService) {
		
		return args -> {
			Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
				CustomerDTO customerDTO = new CustomerDTO();
				customerDTO.setName(name);
				customerDTO.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customerDTO);
			});
			
			bankAccountService.listCustomers().forEach(customer -> {
				
			try {
				bankAccountService
					.saveCurrentBankAccount(Math.random() * 9000, 9000.0, customer.getId());
				bankAccountService
					.saveSavingBankAccount(Math.random() * 15000, 5.5, customer.getId());
				
			} catch (CustomerNotFoundException | BankAccountNotFoundException e) {
				e.printStackTrace();
			}  
			});
			
			List<BankAccountDTO> listAccount = bankAccountService.bankAccountList();
			
			for (BankAccountDTO account : listAccount) {
				for (int i = 0; i < 10; i++) {
					String accountId;
					String customerName;
					if (account instanceof SavingAccountDTO savingAccountDTO) {
						accountId = savingAccountDTO.getId();
						customerName = savingAccountDTO.getCustomerDTO().getName();
					} else {						
						accountId = ((CurrentAccountDTO) account).getId();
						customerName = ((CurrentAccountDTO) account).getCustomerDTO().getName();
					}
					try {
						
						DebitOperationDTO d = new DebitOperationDTO();
						d.setAmount(10000 + Math.random() * 9000);
						d.setDescription("Debit");
						d.setCoveredBy(customerName);
						
						CreditOperationDTO c = new CreditOperationDTO();
						c.setAmount(10000 + Math.random() * 12000);
						c.setDescription("Credit");
						c.setCoveredBy(customerName);
						
						
						bankAccountService.credit(accountId, c);
						bankAccountService.debit(accountId, d);
					} catch (BalanceNotSufficientException e) {
						e.printStackTrace();
					}
				}
			}
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
