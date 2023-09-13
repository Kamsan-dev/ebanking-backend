package com.ebanking.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebanking.app.entites.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

}
