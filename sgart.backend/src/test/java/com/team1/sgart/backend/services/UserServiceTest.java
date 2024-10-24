package com.team1.sgart.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.model.User;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserDao userDao;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Carlos", "Romero Navarro", "Quality", "Ciudad Real", "carlos.romero@example.com", "01/01/2024", 
                        "Scrum Developer", "password123@", "password123@", false);
        
        Mockito.when(userDao.findByEmail(user.getEmail())).thenReturn(null); // Email no registrado
        Mockito.when(userDao.save(user)).thenReturn(user); // Simular guardado de usuario
    }

    @Test
    void testRegistrarUserExitoso() {
        User resultado = userService.registrarUser(user);
        assertEquals(user, resultado);  // Verificar que se devuelve el mismo usuario
    }
}
