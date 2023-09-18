package com.ebanking.app.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ebanking.app.dtos.AccountHistoryDTO;
import com.ebanking.app.dtos.AccountOperationDTO;
import com.ebanking.app.dtos.BankAccountDTO;
import com.ebanking.app.dtos.CreditOperationDTO;
import com.ebanking.app.dtos.CurrentAccountDTO;
import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.dtos.DebitOperationDTO;
import com.ebanking.app.dtos.SavingAccountDTO;
import com.ebanking.app.dtos.TransferOperationDTO;
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
	public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
		log.info("Saving customer");
		return DTOMapper
				.fromCustomer(customerRepository
						.save(DTOMapper.fromCustomerDTO(customerDTO)));
	}
	
	@Override
	public CustomerDTO getCustomer(Long id) {
		Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("CustomerNotFound"));
		return DTOMapper.fromCustomer(customer);
	}
	
	@Override
	public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
		log.info("Updating customer");
		return DTOMapper
				.fromCustomer(customerRepository
						.save(DTOMapper.fromCustomerDTO(customerDTO)));
	}
	
	@Override
	public void deleteCustomer(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("CustomerNotFound"));
		log.info("Deleting customer");
		customerRepository.deleteById(customer.getId());
	}

	@Override
	public List<CustomerDTO> listCustomers() {
		return customerRepository.findAll()
				.stream()
				.map(cust -> DTOMapper.fromCustomer(cust))
				.collect(Collectors.toList());
	}

	@Override
	public BankAccountDTO getBankAccount(String accountId) {
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(() -> 
				new BankAccountNotFoundException("Bank account does not exist"));
		
		if (bankAccount instanceof SavingAccount savingAccount) {
			return DTOMapper.fromSavingAccount(savingAccount);
		} else {
			CurrentAccount currentAccount = (CurrentAccount) bankAccount;
			return DTOMapper.fromCurrentAccount(currentAccount);
		}
	}

	@Override
	public DebitOperationDTO debit(String accountId, DebitOperationDTO request) throws BankAccountNotFoundException, BalanceNotSufficientException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(() -> 
				new BankAccountNotFoundException("Bank account does not exist"));
		if (bankAccount.getBalance() < request.getAmount()) {
			throw new BalanceNotSufficientException("Balance not sufficent");
		}
		
		AccountOperation accountOperation = new AccountOperation();
		accountOperation.setType(OperationType.DEBIT);
		accountOperation.setDescription(request.getDescription());
		accountOperation.setBankAccount(bankAccount);
		accountOperation.setAmount(request.getAmount());
		accountOperation.setOperationDate(new Date());
		accountOperationRepository.save(accountOperation);
		
		bankAccount.setBalance(bankAccount.getBalance() - request.getAmount());
		bankAccountRepository.save(bankAccount);
		return request;
		
	}

	@Override
	public CreditOperationDTO credit(String accountId, CreditOperationDTO request) throws BankAccountNotFoundException{
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(() -> 
				new BankAccountNotFoundException("Bank account does not exist"));
		AccountOperation accountOperation = new AccountOperation();
		accountOperation.setType(OperationType.CREDIT);
		accountOperation.setDescription(request.getDescription());
		accountOperation.setBankAccount(bankAccount);
		accountOperation.setAmount(request.getAmount());
		accountOperation.setOperationDate(new Date());
		accountOperationRepository.save(accountOperation);
		
		bankAccount.setBalance(bankAccount.getBalance() + request.getAmount());
		bankAccountRepository.save(bankAccount);
		return request;
		
	}

	@Override
	public TransferOperationDTO transfer(String accountId, TransferOperationDTO request) throws BankAccountNotFoundException, BalanceNotSufficientException {
		
		String accountIdSource =  accountId;
		String accountIdDestination =  request.getAccountIdDestination();
		
		BankAccount bankAccountSource = bankAccountRepository.findById(accountIdSource)
				.orElseThrow(() -> 
				new BankAccountNotFoundException("Bank account of creditor does not exist"));
		BankAccount bankAccountDestination = bankAccountRepository.findById(accountIdDestination)
				.orElseThrow(() -> 
				new BankAccountNotFoundException("Bank account of receiver does not exist"));
		
		String descriptionFrom = "Transfert from " + accountIdSource 
				+ String.format("\n %s", request.getDescription());
		
		String descriptionTo = "Transfert to " + accountIdDestination
				+ String.format("\n %s", request.getDescription());
		
		
		DebitOperationDTO d = new DebitOperationDTO();
		d.setAmount(request.getAmount());
		d.setDescription(descriptionTo);
		d.setCoveredBy(bankAccountDestination.getCustomer().getName());
		this.debit(accountIdSource, d);
		
		CreditOperationDTO c = new CreditOperationDTO();
		c.setAmount(request.getAmount());
		c.setDescription(descriptionFrom);
		c.setCoveredBy(bankAccountSource.getCustomer().getName());
		this.credit(accountIdDestination, c);
		
		return request;
		
	}

	@Override
	public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long id) {
		Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("CustomerNotFound"));
		
		CurrentAccount currentAccount = new CurrentAccount();
		currentAccount.setId(UUID.randomUUID().toString());
		currentAccount.setCustomer(customer);
		currentAccount.setBalance(initialBalance);
		currentAccount.setStatus(AccountStatus.CREATED);
		currentAccount.setCreatedAt(new Date());
		currentAccount.setOverdraft(overDraft);
		
		return DTOMapper.fromCurrentAccount(bankAccountRepository.save(currentAccount));
	}

	@Override
	public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long id) {
		Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("CustomerNotFound"));
		
		SavingAccount currentAccount = new SavingAccount();
		currentAccount.setId(UUID.randomUUID().toString());
		currentAccount.setCustomer(customer);
		currentAccount.setBalance(initialBalance);
		currentAccount.setStatus(AccountStatus.CREATED);
		currentAccount.setCreatedAt(new Date());
		currentAccount.setInterestRate(interestRate);
		
		return DTOMapper.fromSavingAccount(bankAccountRepository.save(currentAccount));
	}
	
	@Override
	public List<BankAccountDTO> bankAccountList() {
		return bankAccountRepository.findAll()
			.stream()
			.map(account -> {
				if (account instanceof SavingAccount savingAccount) {
					return DTOMapper.fromSavingAccount(savingAccount);
				} else if (account instanceof CurrentAccount currentAccount) {
					return DTOMapper.fromCurrentAccount(currentAccount);
				}
				return null;
			}).collect(Collectors.toList());
	}
	
	@Override
	public List<AccountOperationDTO> accountHistory(String id){
		return accountOperationRepository.findByBankAccountId(id)
				.stream()
				.map(operations -> DTOMapper.fromAccountOperation(operations))
				.collect(Collectors.toList());
	}

	@Override
	public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) {
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(() -> 
				new BankAccountNotFoundException("Bank account does not exist"));
		
		Page<AccountOperation> accountOperations = accountOperationRepository
					.findByBankAccountId(accountId, PageRequest.of(page, size));
		
		AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
		List<AccountOperationDTO> accountOperationDTOs= accountOperations.getContent()
			.stream()
			.map(operations -> DTOMapper.fromAccountOperation(operations))
			.collect(Collectors.toList());
		accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOs);
		accountHistoryDTO.setAccountId(accountId);
		accountHistoryDTO.setBalance(bankAccount.getBalance());
		accountHistoryDTO.setPageSize(size);
		accountHistoryDTO.setCurrentPage(page);
		accountHistoryDTO.setOwner(bankAccount.getCustomer().getName());
		accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
		
		return accountHistoryDTO;
		
	}
}
