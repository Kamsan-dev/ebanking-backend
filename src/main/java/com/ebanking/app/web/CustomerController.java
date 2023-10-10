package com.ebanking.app.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.services.BankAccountService;

//import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/")
public class CustomerController {
	
	private BankAccountService bankAccountService;
	
	@GetMapping("/customers")
	@PreAuthorize("hasAuthority('SCOPE_USER')")
	public List<CustomerDTO> customers(){
		log.info("fetch customer");
		return bankAccountService.listCustomers();
	}
	
	@GetMapping("/customers/search")
	@PreAuthorize("hasAuthority('SCOPE_USER')")
	public List<CustomerDTO> searchCustomers(@RequestParam(name="keyword", defaultValue="") String keyword){
		return bankAccountService.searchCustomers(keyword);
	}
	
	@GetMapping("/customers/{id}")
	@PreAuthorize("hasAuthority('SCOPE_USER')")
	public CustomerDTO getCustomer(@PathVariable(name="id") 
		Long customerId){
		return bankAccountService.getCustomer(customerId);
	}
	
	@PostMapping("/customers")
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	public ResponseEntity<CustomerDTO> saveCustomer(@RequestBody CustomerDTO request) {
		return new ResponseEntity<>(bankAccountService.saveCustomer(request), HttpStatus.CREATED);
	}
	
	@PutMapping("/customers/{id}")
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	public CustomerDTO updateCustomer(@PathVariable(name="id") 
		Long customerId, @RequestBody CustomerDTO customerDTO){
		customerDTO.setId(customerId);
		return bankAccountService.updateCustomer(customerDTO);
	}
	
	@DeleteMapping("/customers/{id}")
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	public void deleteCustomer(@PathVariable(name="id") 
		Long customerId){
		bankAccountService.deleteCustomer(customerId);
	}
	
	
}
