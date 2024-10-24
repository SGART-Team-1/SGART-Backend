package com.team1.sgart.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.model.User;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	public User registrarUser(User user) {
		// Comprobar si el email ya está registrado
		if (emailYaRegistrado(user)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
		}
		else if (!emailFormatoValido(user)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato del email incorrecto");
		} 
		else if (!passwordFormatoValido(user)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de la contraseña incorrecto");
		} 
		else {
			userDao.save(user);
			return user;
		}
    }

	public boolean emailFormatoValido(User user) {
		boolean emailValido = false;
		
		if(user.comprobarFormatoEmail())
			emailValido = true;
		
		return emailValido;
	}
	
	public boolean emailYaRegistrado(User user) {
		boolean yaRegistrado = true;
		
		if (!userDao.findByEmail(user.getEmail()).isPresent())
			yaRegistrado = false;
		
		return yaRegistrado;
	}
	
	public boolean passwordFormatoValido(User user) {
		boolean passwordValido = false;
        
        if(user.comprobarFormatoPassword())
            passwordValido = true;
        
        return passwordValido;
	}
	
	
	
}
