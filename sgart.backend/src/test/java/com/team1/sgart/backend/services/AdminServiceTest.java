package com.team1.sgart.backend.services;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


import com.team1.sgart.backend.dao.AdminDAO;
import com.team1.sgart.backend.dao.UserDAO;
import com.team1.sgart.backend.model.Admin;
import com.team1.sgart.backend.model.User;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private AdminDAO adminDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AdminService adminService;

    @Test
    public void testValidarUsuario_AdminNotFound() {
        // Simular que el administrador no existe
        String adminEmail = "admin@dominio.com";
        Mockito.when(adminDAO.findByEmail(adminEmail))
                .thenReturn(Optional.empty());

        // Ejecutar el test y esperar una excepción
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.validarUsuario("user@dominio.com");
        });

        // Verificar el mensaje de la excepción
        assertEquals("Administrador no encontrado", exception.getMessage());
    }

    @Test
    public void testValidarUsuario_UserNotFound() {
        // Simular que el administrador existe
        String adminEmail = "admin@dominio.com";
        Admin admin = new Admin();
        admin.setEmail(adminEmail);
        Mockito.when(adminDAO.findByEmail(adminEmail))
                .thenReturn(Optional.of(admin));

        // Simular que el usuario no existe
        String userEmail = "user@dominio.com";
        Mockito.when(userDAO.findByEmail(userEmail))
                .thenReturn(Optional.empty());

        // Ejecutar el test y esperar una excepción
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.validarUsuario(userEmail);
        });

        // Verificar el mensaje de la excepción
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    public void testValidarUsuario_UsuarioYaValidado() {
        // Simular que el administrador existe
        String adminEmail = "admin@dominio.com";
        Admin admin = new Admin();
        admin.setEmail(adminEmail);
        Mockito.when(adminDAO.findByEmail(adminEmail))
                .thenReturn(Optional.of(admin));

        // Simular que el usuario existe y ya está validado
        String userEmail = "user@dominio.com";
        User user = new User();
        user.setEmail(userEmail);
        Mockito.when(userDAO.findByEmail(userEmail))
                .thenReturn(Optional.of(user));
        Mockito.when(userDAO.isUsuarioValidado(userEmail))
                .thenReturn(true);

        // Ejecutar el test y esperar una excepción
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.validarUsuario( userEmail);
        });

        // Verificar el mensaje de la excepción
        assertEquals("El usuario ya está validado", exception.getMessage());
    }

    @Test
    public void testValidarUsuario_Exito() {
        // Simular que el administrador existe
        String adminEmail = "admin@dominio.com";
        Admin admin = new Admin();
        admin.setEmail(adminEmail);
        Mockito.when(adminDAO.findByEmail(adminEmail))
                .thenReturn(Optional.of(admin));

        // Simular que el usuario existe y no está validado
        String userEmail = "user@dominio.com";
        User user = new User();
        user.setEmail(userEmail);
        Mockito.when(userDAO.findByEmail(userEmail))
                .thenReturn(Optional.of(user));
        Mockito.when(userDAO.isUsuarioValidado(userEmail))
                .thenReturn(false);

        // Ejecutar el método
        adminService.validarUsuario(userEmail);

        // Verificar que el método de validación fue llamado
        Mockito.verify(userDAO, Mockito.times(1)).validarUsuario(userEmail);
    }
}
