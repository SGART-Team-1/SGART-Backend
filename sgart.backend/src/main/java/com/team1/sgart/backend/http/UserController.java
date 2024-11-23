package com.team1.sgart.backend.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.team1.sgart.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.team1.sgart.backend.services.UserService;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private UserService userService;

	@Autowired
	UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/registro")
	public ResponseEntity<String> registrar(@RequestBody User user) {

		userService.registrarUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
	}

	@PostMapping("/modificar")
	public ResponseEntity<String> modificar(@RequestBody User user) {

		userService.modificarUser(user);
		return ResponseEntity.status(HttpStatus.OK).body("Perfil modificado correctamente");
	}
	
	@PostMapping("/modificarPerfil")
	public ResponseEntity<String> modificarPerfil(@RequestBody UserDTO changesInUser) {
		try {
			userService.modificarPerfilUser(changesInUser);
			return ResponseEntity.status(HttpStatus.OK).body("Perfil modificado correctamente");
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
	}

@PostMapping("/login")
public ResponseEntity<Object> login(@RequestBody User user, HttpSession session) {
    // Autenticar al usuario usando el servicio
    GenericUser genericUser = userService.loginUser(user);
    Map<String, Object> response = new HashMap<>();

    if (genericUser instanceof User) {
        User userLogged = (User) genericUser;
        
        // Validar que el ID del usuario no sea nulo y corresponde al esperado
        UUID userIdFromDb = userLogged.getID();
        if (userIdFromDb == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID de usuario no encontrado en la base de datos");
        }

        // Guardar el ID en la sesión
        session.setAttribute("userId", userIdFromDb);

        // Mapear datos del usuario a un DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setID(userIdFromDb);
        userDTO.setName(userLogged.getName());
        userDTO.setLastName(userLogged.getLastName());
        userDTO.setEmail(userLogged.getEmail());
        userDTO.setPassword(userLogged.getPassword());
        userDTO.setDepartment(userLogged.getDepartment());
        userDTO.setCenter(userLogged.getCenter());
        userDTO.setHiringDate(userLogged.getHiringDate());
        userDTO.setProfile(userLogged.getProfile());
        userDTO.setValidated(userLogged.isValidated());
        userDTO.setBlocked(userLogged.isBlocked());

        // Respuesta para el cliente
        response.put("user", userDTO);
        response.put("type", "user");

        // Log para depuración
        logger.info("Usuario autenticado: {} (ID: {})", userLogged.getEmail(), userIdFromDb);

        return ResponseEntity.ok(response);
    } else if (genericUser instanceof Admin) {
        Admin adminLogged = (Admin) genericUser;

        // Validar que el ID del administrador no sea nulo
        UUID adminIdFromDb = adminLogged.getID();
        if (adminIdFromDb == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID de administrador no encontrado en la base de datos");
        }

        // Guardar el ID en la sesión
        session.setAttribute("userId", adminIdFromDb);

        // Mapear datos del administrador a un DTO
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setID(adminIdFromDb);
        adminDTO.setName(adminLogged.getName());
        adminDTO.setLastName(adminLogged.getLastName());
        adminDTO.setEmail(adminLogged.getEmail());
        adminDTO.setPassword(adminLogged.getPassword());

        // Respuesta para el cliente
        response.put("admin", adminDTO);
        response.put("type", "admin");

        // Log para depuración
        logger.info("Administrador autenticado: {} (ID: {})", adminLogged.getEmail(), adminIdFromDb);

        return ResponseEntity.ok(response);
    } else {
        logger.warn("Intento de inicio de sesión fallido para usuario: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
    }
}


	@PostMapping("/verificar-email")
	public ResponseEntity<String> verificarEmail(@RequestBody User user) {
		boolean existe = userService.emailYaRegistrado(user);
		if (!existe) {
			return ResponseEntity.status(HttpStatus.OK).body("El email no está registrado");
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("El email está registrado");
		}
	}

	@GetMapping("/cargarUsuarios")
	public ResponseEntity<List<UserAbsenceDTO>> getAllUsers() {
		List<UserAbsenceDTO> usersList = userService.loadUsers();
		return ResponseEntity.ok(usersList);
	}

    @GetMapping("/current/userId")
    public ResponseEntity<Map<String, UUID>> getCurrentUserId(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, UUID> response = new HashMap<>();
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

}
