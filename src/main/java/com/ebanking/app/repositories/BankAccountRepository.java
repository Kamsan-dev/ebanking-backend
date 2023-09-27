package com.ebanking.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebanking.app.entites.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, String>{
	
	List<BankAccount> findAccountsByCustomerId(String customerId);

}
