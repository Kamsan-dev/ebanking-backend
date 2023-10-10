package com.ebanking.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ebanking.app.entites.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{
	
	@Query("SELECT c FROM Customer c WHERE c.name ilike :kw")
	List<Customer> searchCustomers(@Param(value="kw")String keyword);

}
