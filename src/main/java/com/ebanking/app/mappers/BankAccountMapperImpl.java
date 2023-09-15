package com.ebanking.app.mappers;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.ebanking.app.dtos.AccountOperationDTO;
import com.ebanking.app.dtos.CurrentAccountDTO;
import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.dtos.SavingAccountDTO;
import com.ebanking.app.entites.AccountOperation;
import com.ebanking.app.entites.CurrentAccount;
import com.ebanking.app.entites.Customer;
import com.ebanking.app.entites.SavingAccount;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BankAccountMapperImpl {
	
	public CustomerDTO fromCustomer(Customer customer) {
		CustomerDTO customerDTO = new CustomerDTO();
		BeanUtils.copyProperties(customer, customerDTO);
		return  customerDTO;
	}
	
	public Customer fromCustomerDTO(CustomerDTO customerDTO) {
		Customer customer = new Customer();
		BeanUtils.copyProperties(customerDTO, customer);
		return  customer;
	}
	
	public SavingAccountDTO fromSavingAccount(SavingAccount savingAccount) {
		SavingAccountDTO savingAccountDTO = new SavingAccountDTO();
		BeanUtils.copyProperties(savingAccount, savingAccountDTO);
		savingAccountDTO.setCustomerDTO(this.fromCustomer(savingAccount.getCustomer()));
		savingAccountDTO.setType(savingAccount.getClass().getSimpleName());
		return savingAccountDTO;
		
	}
	
	public SavingAccount fromSavingAccountDTO(SavingAccountDTO savingAccountDTO) {
		SavingAccount savingAccount = new SavingAccount();
		BeanUtils.copyProperties(savingAccountDTO, savingAccount);
		savingAccount.setCustomer(this.fromCustomerDTO(savingAccountDTO.getCustomerDTO()));
		return savingAccount;
	}
	
	public CurrentAccountDTO fromCurrentAccount(CurrentAccount currentAccount) {
		CurrentAccountDTO currentAccountDTO = new CurrentAccountDTO();
		BeanUtils.copyProperties(currentAccount, currentAccountDTO);
		currentAccountDTO.setCustomerDTO(this.fromCustomer(currentAccount.getCustomer()));
		currentAccountDTO.setType(currentAccount.getClass().getSimpleName());
		currentAccountDTO.setOverDraft(currentAccount.getOverdraft());
		return currentAccountDTO;
	}
	
	public CurrentAccount fromCurrentAccountDTO(CurrentAccountDTO currentAccountDTO) {
		CurrentAccount currentAccount = new CurrentAccount();
		BeanUtils.copyProperties(currentAccountDTO, currentAccount);
		currentAccount.setCustomer(this.fromCustomerDTO(currentAccountDTO.getCustomerDTO()));
		return currentAccount;
	}
	
	public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation) {
		AccountOperationDTO Aodto = new AccountOperationDTO();
		BeanUtils.copyProperties(accountOperation, Aodto);
		return Aodto;
	}
	
	public AccountOperation fromAccountOperationDTO(AccountOperationDTO accountOperationDTO) {
		return null;
			
	}

}
