package com.ebanking.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebanking.app.entites.AccountOperation;

public interface AccountOperationRepository extends JpaRepository<AccountOperation	, Long>{

}
