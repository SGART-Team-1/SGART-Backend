package com.team1.sgart.backend.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.team1.sgart.backend.model.User;

public interface UserDAO extends JpaRepository<User, Integer> {  
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);

    // Método para verificar si el usuario está validado
    @Query("SELECT u.validated FROM User u WHERE u.email = :email")  
    Boolean isUsuarioValidado(@Param("email") String email);

    // Método para marcar al usuario como validado
    @Modifying
    @Query("UPDATE User u SET u.validated = true WHERE u.email = :email")
    void validarUsuario(@Param("email") String email);
}
