package com.ebanking.app.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ebanking.app.entites.AppRole;
import com.ebanking.app.entites.AppUser;
import com.ebanking.app.security.CustomUser;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

	private AccountService accountService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser appUser = accountService.loadUserByUsername(username);
		if (appUser == null)
			throw new UsernameNotFoundException(String.format("User %s not found", username));

		/* role.getRole() -> (get libelle) */
		String[] roles = appUser.getRoles().stream().map(role -> role.getRole()).toArray(String[]::new);

//		UserDetails userDetails = User
//				.withUsername(appUser.getUsername())
//				.password(appUser.getPassword())
//				.authorities(roles)
//				.build();

		CustomUser userDetails3 = new CustomUser(appUser.getCustomer_id().getId(), appUser.getUsername(),
				appUser.getPassword(), buildUserAuthority(roles));
		return userDetails3;

	}

	private List<GrantedAuthority> buildUserAuthority(String[] roles) {

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		for (String role : roles) {

			setAuths.add(new SimpleGrantedAuthority(role));

		}

		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);

		return Result;
	}

}
