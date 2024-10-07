package com.team1.sgart.backend.model;

public class User {
	
	private String name;
    private String lastName; 
    private String department;
    private String center;
    private String email;
    private String hiringDate;
    private String profile;
    private String password;
    private String passwordConfirm;
    private boolean internal;
    private boolean blocked;
    
	public User(String name, String lastName, String department, String center, String email, String hiringDate,
			String profile, String password, String passwordConfirm, boolean internal, boolean blocked) {
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
		this.internal = internal;
		this.blocked = blocked;
	}
	
	public User() {
		// TODO Auto-generated constructor stub
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
	public boolean isInternal() {
		return internal;
	}
	public void setInternal(boolean internal) {
		this.internal = internal;
	}
	public boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

}