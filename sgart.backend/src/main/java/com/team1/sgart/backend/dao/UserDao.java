package com.team1.sgart.backend.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team1.sgart.backend.model.User;

<<<<<<< Updated upstream
public interface UserDao extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
=======
import jakarta.transaction.Transactional;

public interface UserDAO extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);

	Optional<User> findByEmailAndPassword(String email, String password);
>>>>>>> Stashed changes
    
    Optional<User> findByEmailAndPassword(String email, String password);
}