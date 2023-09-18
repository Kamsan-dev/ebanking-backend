package com.ebanking.app.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerDTO {
	
	private Long id;
	@NotNull @NotBlank(message = "Name should not be null")
	private String name;
	@Email (message = "Invalid email adress")
	private String email;
}
