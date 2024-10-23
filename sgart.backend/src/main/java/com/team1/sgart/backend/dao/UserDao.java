package com.team1.sgart.backend.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team1.sgart.backend.model.User;

import jakarta.transaction.Transactional;

public interface UserDAO extends JpaRepository<User, String> {
	Optional<User> findByEmail(String email);

	Optional<User> findByEmailAndPassword(String email, String password);
    
	// Método para verificar si el usuario está validado
    @Query("SELECT u.validated FROM User u WHERE u.email = :email")
    Boolean isUsuarioValidado(@Param("email") String email);

    // Método para marcar al usuario como validado
    @Modifying
    @Query("UPDATE User u SET u.validated = true WHERE u.email = :email")
    void validarUsuario(@Param("email") String email);

    // Método para invertir el valor de "blocked" de un usuario
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.blocked=NOT u.blocked WHERE u.email = :email")
    void cambiarHabilitacionUsuario(@Param("email") String email);
}

