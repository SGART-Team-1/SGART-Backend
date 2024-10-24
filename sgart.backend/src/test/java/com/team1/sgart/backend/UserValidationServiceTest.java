package com.team1.sgart.backend;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserValidationServiceTest {
    @Test
    protected void testApproveRegistrationWithValidRequest() {
        // Simular repositorios
        RegistrationRequestRepository requestRepo = new RegistrationRequestRepository();
        UserRepository userRepo = new UserRepository();
        UserValidationService validationService = new UserValidationService(requestRepo, userRepo);

        // Crear una solicitud de registro
        RegistrationRequest request = new RegistrationRequest("testUser", "password123", "test@example.com");
        requestRepo.addRequest(request);

        // Aprobar el registro
        boolean result = validationService.approveRegistration("testUser");

        // Verificar si el usuario fue creado y la solicitud eliminada
        assertTrue(result);
        assertNotNull(userRepo.findUserByUsername("testUser"));
        assertNull(requestRepo.findRequestByUsername("testUser"));
    }

    @Test
    public void testApproveRegistrationWithInvalidRequest() {
        // Simular repositorios
        RegistrationRequestRepository requestRepo = new RegistrationRequestRepository();
        UserRepository userRepo = new UserRepository();
        UserValidationService validationService = new UserValidationService(requestRepo, userRepo);

        // Intentar aprobar una solicitud inexistente
        boolean result = validationService.approveRegistration("nonExistentUser");

        // Verificar que no se apruebe
        assertFalse(result);
        assertNull(userRepo.findUserByUsername("nonExistentUser"));
    }

    @Test
    public void testApproveRegistrationAlreadyApproved() {
        // Simular repositorios
        RegistrationRequestRepository requestRepo = new RegistrationRequestRepository();
        UserRepository userRepo = new UserRepository();
        UserValidationService validationService = new UserValidationService(requestRepo, userRepo);

        // Crear y aprobar una solicitud de registro
        RegistrationRequest request = new RegistrationRequest("testUser", "password123", "test@example.com");
        request.setApproved(true);
        requestRepo.addRequest(request);

        // Intentar aprobar una solicitud ya aprobada
        boolean result = validationService.approveRegistration("testUser");

        // Verificar que no se apruebe de nuevo
        assertFalse(result);
    }
}

