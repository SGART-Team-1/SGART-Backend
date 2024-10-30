package com.team1.sgart.backend.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team1.sgart.backend.model.Admin;

public interface AdminDao extends  JpaRepository<Admin, Integer> {
	    Optional<Admin> findByEmail(String email);
	    Optional<Admin> findByEmailAndPassword(String email, String password);
}
