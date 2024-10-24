package com.team1.sgart.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GenericUser {

	@Id
	@Column
	private int id;

	@Column(nullable = false)
	protected String name;
	
	@Column(name = "LastName", nullable = false)
	protected String lastName;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(name = "Password", nullable = false)
	protected String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// Getters y setters
}

