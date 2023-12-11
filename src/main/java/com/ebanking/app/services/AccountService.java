package com.ebanking.app.services;

import com.ebanking.app.entites.AppRole;
import com.ebanking.app.entites.AppUser;

public interface AccountService {
	
	AppUser addNewUser(String username, String password, String email, String confirmPassword, long customerid);
	AppRole addNewRole(String role);
	void addRoleToUser(String username, String role);
	void removeRoleFromUser(String username, String role);
	AppUser loadUserByUsername(String username);
	
}
