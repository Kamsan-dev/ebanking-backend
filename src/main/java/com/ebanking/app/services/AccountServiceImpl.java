package com.ebanking.app.services;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.entites.AppRole;
import com.ebanking.app.entites.AppUser;
import com.ebanking.app.entites.Customer;
import com.ebanking.app.exceptions.UsernameAlreadyTakenException;
import com.ebanking.app.repositories.AppRoleRepository;
import com.ebanking.app.repositories.AppUserRepository;
import com.ebanking.app.repositories.CustomerRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

	private AppUserRepository appUserRepository;
	private AppRoleRepository appRoleRepository;
	private CustomerRepository customerRepository;
	//private PasswordEncoder passwordEncoder;

	@Override
	public AppUser addNewUser(String username, String password, String email, String confirmPassword, long customerId) {
		AppUser user = appUserRepository.findByUsername(username);
		if (user != null)
			throw new UsernameAlreadyTakenException("Username is already taken");

//		if (!password.equals(confirmPassword)) {
//			throw new RuntimeException("Password not match");
//		}
	
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow();

		AppUser appUser = new AppUser();
		appUser.setUserId(UUID.randomUUID().toString());
		appUser.setUsername(username);
		appUser.setPassword(password);
		appUser.setEmail(email);
		appUser.setCustomer_id(customer);
		return appUserRepository.save(appUser);
	}

	@Override
	public AppRole addNewRole(String role) {
		AppRole savedRole = new AppRole();
		savedRole.setRole(role);
		return appRoleRepository.save(savedRole);
	}

	@Override
	public void addRoleToUser(String username, String role) {
		AppUser appUser = appUserRepository.findByUsername(username);

		AppRole appRole = appRoleRepository.findById(role)
				.orElseThrow(() -> new RuntimeException("Role does not exist"));

		appUser.getRoles().add(appRole);
		// appUserRepository.save(appUser); not needed since @transactionnal

	}

	@Override
	public void removeRoleFromUser(String username, String role) {
		AppUser appUser = appUserRepository.findByUsername(username);

		AppRole appRole = appRoleRepository.findById(role)
				.orElseThrow(() -> new RuntimeException("Role does not exist"));

		appUser.getRoles().remove(appRole);
	}

	@Override
	public AppUser loadUserByUsername(String username) {
		return appUserRepository.findByUsername(username);
	}
}
