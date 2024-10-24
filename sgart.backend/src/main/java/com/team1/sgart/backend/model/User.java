package com.team1.sgart.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.util.Date;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "SGART_UsersTable", indexes = @Index(columnList = "id", unique = true))
public class User extends GenericUser{
	
	@Column(name = "Department")
	private String department;
	
	@Column(name = "Center", nullable = false)
	private String center;
	
	@Column(name = "HiringDate", nullable = false)
	private String hiringDate;
	
	@Column(name = "Profile")
	private String profile;
	
	@Transient
	private String passwordConfirm;
  
    @Column(name = "Validated", nullable = false)
    private boolean validated = false;
	
    @Column(name = "Blocked", nullable = false)
	private boolean blocked;

	public User(String name, String lastName, String department, String center, String email, String hiringDate,
			String profile, String password, String passwordConfirm, boolean blocked) {
		super();
		this.name = name;
		this.lastName = lastName;
		this.department = department;
		this.center = center;
		this.email = email;
		this.hiringDate = hiringDate;
		this.profile = profile;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
		this.blocked = blocked;
		this.validated = validated;
	}

	public User() {
	}

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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHiringDate() {
		return hiringDate;
	}

	public void setHiringDate(String hiringDate) {
		this.hiringDate = hiringDate;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean comprobarFormatoPassword() {
		boolean valida = true;
		
		int longitudMinima = 8;
	    String mayus = ".*[A-Z].*";
	    String minus = ".*[a-z].*";
	    String digit = ".*\\d*.";
	    String specialCharacters = ".*[!@#\\$%\\^&\\*].*";


	    // Verificar longitud mínima
	    if (password.length() < longitudMinima) {
	    	valida = false;
	    }

	    // Verificar que tenga al menos una letra mayúscula
	    if (!Pattern.matches(mayus, password)) {
	    	valida = false;
	    }

	    // Verificar que tenga al menos una letra minúscula
	    if (!Pattern.matches(minus, password)) {
	    	valida = false;
	    }
	    
	    // Verificar que tenga al menos un dígito
	    if (!Pattern.matches(digit, password)) {
	    	valida = false;
	    }

	    // Verificar que tenga al menos un carácter especial
	    if (!Pattern.matches(specialCharacters, password)) {
	    	valida = false;
	    }

	    // Si cumple con todas las condiciones
	    return valida;
	}

	public boolean comprobarFormatoEmail() {
		boolean valido = true;
		String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		if (!Pattern.matches(emailPattern, email)) {
			valido = false;
		}
		return valido;
	}

	public boolean comprobarFormatoFecha() {
        boolean valido = true;
        String fechaPattern = "^\\d{2}/\\d{2}/\\d{4}$";
		if (!Pattern.matches(fechaPattern, hiringDate)) {
			valido = false;
		}
        return valido;
    }

}
