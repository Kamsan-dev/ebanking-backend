package com.ebanking.app.mappers;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.entites.Customer;

@Service
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

}