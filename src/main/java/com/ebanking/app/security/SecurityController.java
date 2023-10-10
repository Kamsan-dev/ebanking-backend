package com.ebanking.app.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class SecurityController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtEncoder jwtEncoder;

	/* Get profil of authentified user */
	@GetMapping("/profile")
	public Authentication authentication(Authentication authentication) {
		return authentication;
	}

	@PostMapping("/login")
	public Map<String, String> login(String username, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		/* jwt token creation */
		String scope = authentication.getAuthorities().stream().map(a -> a.getAuthority())
				.collect(Collectors.joining(" "));
		Instant instant = Instant.now();
		JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder().issuedAt(instant)
				.expiresAt(instant.plus(10, ChronoUnit.MINUTES)).subject(username).claim("scope", scope).build();

		JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters
				.from(JwsHeader.with(MacAlgorithm.HS512).build(), jwtClaimsSet);
		String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
		//return Map.of("username", username, "access-token", jwt, "roles", scope);
		return Map.of("access-token", jwt);
	}

}
