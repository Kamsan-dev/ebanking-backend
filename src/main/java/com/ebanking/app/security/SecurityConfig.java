package com.ebanking.app.security;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.ebanking.app.dtos.CustomerDTO;
import com.ebanking.app.entites.Customer;
import com.ebanking.app.repositories.CustomerRepository;
import com.ebanking.app.services.AccountService;
import com.ebanking.app.services.BankAccountService;
import com.ebanking.app.services.UserDetailServiceImpl;
import com.nimbusds.jose.jwk.source.ImmutableSecret;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	@Value("${jwtsecret}")
	private String secretKey;
	
	private UserDetailServiceImpl userDetailsServiceImpl;
	
	
	@Autowired
	public SecurityConfig(UserDetailServiceImpl userDetailsServiceImpl) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
	}

	//@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource ) {
		return new JdbcUserDetailsManager(dataSource);
	}
	
	//@Bean
	CommandLineRunner users(JdbcUserDetailsManager jdbcUserDetailsManager) {
		PasswordEncoder passwordEncoder = passwordEncoder();
		return args -> {
			jdbcUserDetailsManager.createUser(
					User.withUsername("user1").password(passwordEncoder.encode("12345")).authorities("USER").build());
			jdbcUserDetailsManager.createUser(
					User.withUsername("admin").password(passwordEncoder.encode("admin")).authorities("USER", "ADMIN").build());
		};
	}

	//@Bean
	public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
		PasswordEncoder passwordEncoder = passwordEncoder();
		return new InMemoryUserDetailsManager(
				User.withUsername("user1").password(passwordEncoder.encode("12345")).authorities("USER").build(),
				User.withUsername("admin").password(passwordEncoder.encode("admin")).authorities("USER", "ADMIN")
						.build());

	}
	
	@Bean
	CommandLineRunner userDetails(AccountService accountService, BankAccountService bankAccountService) {
		PasswordEncoder passwordEncoder = passwordEncoder();
		return args -> {
			
			Stream.of("Hassan", "Yassine", "Aicha", "ADMIN").forEach(name -> {
				CustomerDTO customerDTO = new CustomerDTO();
				customerDTO.setName(name);
				customerDTO.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customerDTO);
			});

			accountService.addNewRole("USER");
			accountService.addNewRole("ADMIN");
			accountService.addNewUser("user1", passwordEncoder.encode("12345"), "user1@gmail.com", passwordEncoder.encode("12345"), 1);
			accountService.addNewUser("user2", passwordEncoder.encode("12345"), "user1@gmail.com", passwordEncoder.encode("12345"), 2);
			accountService.addNewUser("user3", passwordEncoder.encode("12345"), "user1@gmail.com", passwordEncoder.encode("12345"), 3);
			accountService.addNewUser("admin", passwordEncoder.encode("admin"), "admin@gmail.com", passwordEncoder.encode("admin"), 4);
			
			accountService.addRoleToUser("user1", "USER");
			accountService.addRoleToUser("user2", "USER");
			accountService.addRoleToUser("user3", "USER");
			
			accountService.addRoleToUser("admin", "ADMIN"); 
			accountService.addRoleToUser("admin", "USER");
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		return httpSecurity.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(ar->ar.requestMatchers("api/v1/auth/login/**").permitAll())
				.authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
				//.httpBasic(Customizer.withDefaults())
				.oauth2ResourceServer(oa -> oa.jwt(Customizer.withDefaults()))
				//.userDetailsService(userDetailsServiceImpl)
				.build();
	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> web.ignoring()
	                       .requestMatchers("/api/v1/auth/login/**")
	                       .requestMatchers("/v3/api-docs/**")
	                       .requestMatchers("configuration/**")
	                       .requestMatchers("/swagger*/**")
	                       .requestMatchers("/webjars/**")
	                       .requestMatchers("/swagger-ui/**");
	}

	@Bean
	JwtEncoder jwtEncoder() {
		//String secretKey = "heqkqz18x6dozoafuhbgjj2tz5hip5v1t2svv44g22d6yi4c3i6r22cglvwa6bch";
		return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey.getBytes()));
	}

	@Bean
	JwtDecoder jwtDecoder() {
		//String secretKey = "heqkqz18x6dozoafuhbgjj2tz5hip5v1t2svv44g22d6yi4c3i6r22cglvwa6bch";
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "RSA");
		return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl);
		return new ProviderManager(daoAuthenticationProvider);
	}
	
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

}
