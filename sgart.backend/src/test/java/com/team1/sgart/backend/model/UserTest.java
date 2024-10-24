package com.team1.sgart.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("Carlos", "Romero Navarro", "Quality", "Ciudad Real", "carlos.romero@example.com", "01/01/2024", 
                        "Scrum Developer", "password123@", "password123@", false);
    }

    @Test
    public void testConstructor() {
        assertEquals("Carlos", user.getName());
        assertEquals("Romero Navarro", user.getLastName());
        assertEquals("Quality", user.getDepartment());
        assertEquals("Ciudad Real", user.getCenter());
        assertEquals("carlos.romero@example.com", user.getEmail());
        assertEquals("01/01/2024", user.getHiringDate());
        assertEquals("Scrum Developer", user.getProfile());
        assertEquals("password123@", user.getPassword());
        assertEquals("password123@", user.getPasswordConfirm());
        assertFalse(user.isBlocked());
    }

    @Test
    public void testSetName() {
        user.setName("Juan");
        assertEquals("Juan", user.getName());
    }

    @Test
    public void testSetLastName() {
        user.setLastName("Garcia");
        assertEquals("Garcia", user.getLastName());
    }

    @Test
    public void testPasswordConfirmationMatches() {
        assertEquals(user.getPassword(), user.getPasswordConfirm(), 
                     "Password and passwordConfirm should match");
    }

    @Test
    public void testBlockUser() {
        user.setBlocked(true);
        assertTrue(user.isBlocked());
    }

    @Test
    public void testSetEmail() {
        user.setEmail("new.email@example.com");
        assertEquals("new.email@example.com", user.getEmail());
    }

    @Test
    public void testSetProfile() {
        user.setProfile("User");
        assertEquals("User", user.getProfile());
    }
    
    @Test
	public void testSetPassword() {
		user.setPassword("newPassword123");
		assertEquals("newPassword123", user.getPassword());
	}
    
    @Test
	public void testComprobarFormatoPasswordCorrecto() {
		user.setPassword("Password123@");
		assertTrue(user.comprobarFormatoPassword());
	}
    
    @Test
    public void testComprobarFormatoPasswordIncorrecto() {
		user.setPassword("password");
		assertFalse(user.comprobarFormatoPassword());
	}
    
    @Test
    public void testComprobarFormatoEmailCorrecto() {
    	user.setEmail("user@example.com");
    	assertTrue(user.comprobarFormatoEmail());
    }
    
    @Test 
    public void testComprobarFormatoEmailIncorrecto() {
    	user.setEmail("userexample.com");
    	assertFalse(user.comprobarFormatoEmail());
    }
    
    @Test
	public void testComprobarFormatoFechaCorrecto() {
		user.setHiringDate("01/01/2024");
		assertTrue(user.comprobarFormatoFecha());
	}
    
    @Test
    public void testComprobarFormatoFechaIncorrecto() {
    	user.setHiringDate("01-01-24");
    	assertFalse(user.comprobarFormatoFecha());
    }
}
