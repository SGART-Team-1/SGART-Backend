package com.team1.sgart.backend.http;

import java.util.Date;

public class RegistrationRequest {
    private String name;
    private String lastName;
    private String department;
    private String center;
    private Date hiringDate;
    private String profile;
    private String email;
    private String password;
    
	
	public RegistrationRequest(String name, String lastName, String department, String center, Date hiringDate,
			String profile, String email, String password) {
		
		this.name = name;
		this.lastName = lastName;
		this.department = department;
		this.center = center;
		this.hiringDate = hiringDate;
		this.profile = profile;
		this.email = email;
		this.password = password;
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
	public Date getHiringDate() {
		return hiringDate;
	}
	public void setHiringDate(Date hiringDate) {
		this.hiringDate = hiringDate;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
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
