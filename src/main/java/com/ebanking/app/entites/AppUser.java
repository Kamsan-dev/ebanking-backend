package com.ebanking.app.entites;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AppUser {
	
	@Id
	private String userId;
	@Column(unique = true)
	private String username;
	private String password;
	private String email;
	
	@OneToOne
	private Customer customer_id;
	
	/* creation table association pour associer utilisateurs et roles */
	/* chargement des 
	roles uniquements sur 
	demande de l'utilisateur 
	(via getter)  = LAZY */
	
	@ManyToMany(fetch = FetchType.EAGER) 
	private List<AppRole> roles;

}
