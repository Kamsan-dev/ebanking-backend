package com.ebanking.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebanking.app.entites.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String>{

	AppUser findByUsername(String username);
}
