package com.team1.sgart.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team1.sgart.backend.dao.UserDAO;

import com.team1.sgart.backend.model.User;

@Service
public class AdminService {

	@Autowired
	private UserDAO userDAO;

	public void validarUsuario(String userEmail) {

		// Buscar al usuario por email
		User user = userDAO.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

		// Validar al usuario si no está validado
		if (!isUserValidated(user)) {
			userDAO.validarUsuario(userEmail);
		} else {
			throw new IllegalArgumentException("El usuario ya está validado");
		}
	}
	
	public void cambiarHabilitacionUsuario(String userEmail){
		if(!userDAO.findByEmail(userEmail).isPresent())
			throw new IllegalArgumentException();
        userDAO.cambiarHabilitacionUsuario(userEmail);
    }

	private boolean isUserValidated(User user) {
		// Verificar si el usuario ya está validado en la base de datos
		return userDAO.isUsuarioValidado(user.getEmail());
	}
}
