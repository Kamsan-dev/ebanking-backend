package com.ebanking.app.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ebanking.app.entites.AccountOperation;

public interface AccountOperationRepository extends JpaRepository<AccountOperation	, Long>{
	List<AccountOperation> findByBankAccountId(String accountId);
	Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String accountId, PageRequest pageRequest);
}
