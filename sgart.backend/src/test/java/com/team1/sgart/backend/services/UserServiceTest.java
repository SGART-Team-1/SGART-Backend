package com.team1.sgart.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import com.team1.sgart.backend.dao.AdminDao;
import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.model.Admin;
import com.team1.sgart.backend.model.GenericUser;
import com.team1.sgart.backend.model.User;
import com.team1.sgart.backend.model.UserDTO;

@SpringBootTest
class UserServiceTest {
	
	private static final String PASSWORD_DEBIL = "password";
	private static final String NUEVO_NOMBRE = "Pablo";
	private static final String PASSWORD_FUERTE = "Password365@";
	private static final String FECHA_CORRECTA = "01/01/2024";
	private static final String PASSWORD_INCORRECTO = "wrongPassword";
	private static final String EMAIL_USUARIO = "carlos.romero@example.com";

    @Autowired
    private UserService userService;

    @MockBean
    private UserDao userDao;
    
    @MockBean
    private AdminDao adminDao;

    private User user;
    private Admin admin;
    private UserDTO jsonPerfilModificado = new UserDTO();

    @BeforeEach
    void setUp() {
    	user = new User("carlos.romero@example.com", "Carlos", "Romero Navarro", "Quality", "Ciudad Real", FECHA_CORRECTA, 
                "Scrum Developer", PASSWORD_FUERTE, PASSWORD_FUERTE, false, false, "");
    	
    	admin = new Admin("admin", "example", "admin@example.com", PASSWORD_FUERTE);
    	
    	jsonPerfilModificado.setID(user.getID());
    	jsonPerfilModificado.setName("John");
    	jsonPerfilModificado.setLastName("Marston");
    	jsonPerfilModificado.setDepartment("Quality");
    	jsonPerfilModificado.setCenter("Royal City");
    	jsonPerfilModificado.setProfile("Scrum Master");
        
        Mockito.when(userDao.findByEmail(user.getEmail())).thenReturn(Optional.empty()); // Email no registrado
        Mockito.when(userDao.save(user)).thenReturn(user); // Simular guardado de usuario
    }

    @Test
    void testRegistrarUserExitoso() {
        User resultado = userService.registrarUser(user);
        assertEquals(user, resultado);  // Verificar que se devuelve el mismo usuario
    }

    @Test
    void testRegistrarUserEmailYaRegistrado() {
    	
    	Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userDao.findByEmail(user.getEmail())).thenReturn(optionalUser); // Simular que el email ya está registrado
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userService.registrarUser(user)
        );
        assertEquals("El email ya está registrado", exception.getReason());
    }

    @Test
    void testRegistrarUserPasswordDebil() {
    	
        user.setPassword(PASSWORD_DEBIL); // Contraseña débil
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userService.registrarUser(user)
        );

        assertEquals("Formato de la contraseña incorrecto", exception.getReason());
    }

    @Test
    void testRegistrarUserEmailFormatoInvalido() {
        user.setEmail("carlos.romero"); // Email con formato inválido
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userService.registrarUser(user)
        );

        assertEquals("Formato del email incorrecto", exception.getReason());
    }
    
    @Test
    void testModificarUserExistente() {
        // Datos de prueba
        Optional<User> optionalUser = Optional.of(user);

        User userModificado = new User();
        userModificado.setEmail("carlos.romero@example.com");
        userModificado.setName(NUEVO_NOMBRE);

        // Configurar los mocks
        Mockito.when(userDao.findByEmail(user.getEmail())).thenReturn(optionalUser); // Simular que el usuario existe
        Mockito.when(userDao.save(Mockito.any(User.class))).thenReturn(userModificado); // Simular guardado de usuario

        // Ejecutar el método
        userService.modificarUser(userModificado);

        // Verificar que el perfil ha sido actualizado
        assertEquals(NUEVO_NOMBRE, user.getName());
        Mockito.verify(userDao).save(user);
    }

    @Test
    void testModificarUserNoExistente() {
    	User userModificado = new User();
        userModificado.setName(NUEVO_NOMBRE);
        userModificado.setEmail("romero@example.com");

        assertThrows(ResponseStatusException.class, () -> 
            userService.modificarUser(userModificado)
        );
    }
    
    @Test
    void testLoginUsuarioValido() {
    	Optional<User> optionalUser = Optional.of(user);
    	Mockito.when(userDao.findByEmailAndPassword("carlos.romero@example.com", PASSWORD_FUERTE)).thenReturn(optionalUser);
    	Mockito.when(userDao.findByEmail("carlos.romero@example.com")).thenReturn(optionalUser);

        // Ejecución
        GenericUser resultado = userService.loginUser(user);
        User userLogged = (User) resultado;

        // Verificación
        assertEquals(user, userLogged);
    }

    @Test
    void testLoginAdminValidoDevuelveAdmin() {
        Optional<Admin> optionalAdmin = Optional.of(admin);
        Mockito.when(adminDao.findByEmailAndPassword("admin@example.com", PASSWORD_FUERTE)).thenReturn(optionalAdmin);
        Mockito.when(adminDao.findByEmail("admin@example.com")).thenReturn(optionalAdmin);

        // Ejecución
        User adminUser = new User("admin@example.com", "", "", "", "", "", "", PASSWORD_FUERTE, "", false, false, null);
        GenericUser resultado = userService.loginUser(adminUser);
        Admin adminLogged = (Admin) resultado;

        // Verificación
        assertEquals(admin, adminLogged);
    }

    @Test
    void testLoginUsuarioNoEncontradoDevuelve401() {
        // Configuración del mock para simular usuario no encontrado
        Mockito.when(userDao.findByEmailAndPassword(EMAIL_USUARIO, PASSWORD_INCORRECTO)).thenReturn(Optional.empty());
        Mockito.when(adminDao.findByEmailAndPassword(EMAIL_USUARIO, PASSWORD_INCORRECTO)).thenReturn(Optional.empty());

        // Ejecución y verificación
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userService.loginUser(user)
        );

        assertEquals("Usuario o contraseña incorrectos", exception.getReason());
    }

    @Test
    void testLoginEmailFormatoInvalidoDevuelve400() {
        // Configuración del email con formato inválido
        user.setEmail("carlos.romero");

        // Ejecución y verificación
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userService.loginUser(user)
        );

        assertEquals("Formato del email incorrecto", exception.getReason());
    }
    
    @Test
    void testModificarPerfilUserExistente() {
        // Datos de prueba
        Optional<User> optionalUser = Optional.of(user);

        User userPerfilModificado = new User();
        userPerfilModificado.setID(user.getID());
        userPerfilModificado.setName(jsonPerfilModificado.getName());
        userPerfilModificado.setLastName(jsonPerfilModificado.getLastName());
        userPerfilModificado.setDepartment(jsonPerfilModificado.getDepartment());
        userPerfilModificado.setCenter(jsonPerfilModificado.getCenter());
        userPerfilModificado.setProfile(jsonPerfilModificado.getProfile());

        // Configurar los mocks
        Mockito.when(userDao.findById(user.getID())).thenReturn(optionalUser); // Simular que el usuario existe
        Mockito.when(userDao.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Simular guardado de usuario

        // Ejecutar el método
        userService.modificarPerfilUser(jsonPerfilModificado);

        // Verificar que el perfil ha sido actualizado
        assertEquals("John", user.getName());
        assertEquals("Marston", user.getLastName());
        assertEquals("Quality", user.getDepartment());
        assertEquals("Royal City", user.getCenter());
        assertEquals("Scrum Master", user.getProfile());
        Mockito.verify(userDao).save(user); // Verificar que el perfil ha sido actualizado
    }

    @Test
    void testModificarPerfilUserNoExistente() {
    	User userModificado = new User();
    	userModificado.setID(UUID.randomUUID());
        userModificado.setName(NUEVO_NOMBRE);
        userModificado.setEmail("romero@example.com");

        assertThrows(ResponseStatusException.class, () -> 
            userService.modificarUser(userModificado)
        );
    }

}
