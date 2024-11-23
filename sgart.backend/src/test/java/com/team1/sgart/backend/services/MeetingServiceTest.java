package com.team1.sgart.backend.services;

import com.team1.sgart.backend.dao.InvitationsDao;
import com.team1.sgart.backend.dao.MeetingsDao;
import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.model.Invitations;
import com.team1.sgart.backend.model.InvitationStatus;
import com.team1.sgart.backend.model.Meetings;
import com.team1.sgart.backend.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

	@Mock
	private MeetingsDao meetingDao;

	@Mock
	private UserDao userDao;

	@Mock
	private InvitationsDao invitationDao;

	@InjectMocks
	private MeetingService meetingService;

	private Meetings existingMeeting;
	private Meetings updatedMeeting;

	public MeetingServiceTest() {
		MockitoAnnotations.openMocks(this);
	}

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		existingMeeting = new Meetings("Initial Meeting", LocalDate.now().plusDays(1), false, LocalTime.of(9, 0),
				LocalTime.of(10, 0), "Initial Observations", UUID.randomUUID(), UUID.randomUUID());
		existingMeeting.setMeetingId(UUID.randomUUID());

		updatedMeeting = new Meetings("Updated Meeting", LocalDate.now().plusDays(2), false, LocalTime.of(10, 0),
				LocalTime.of(11, 0), "Updated Observations", existingMeeting.getOrganizerId(),
				existingMeeting.getLocationId());
	}

	@Test
    void createMeeting_ShouldSaveAndReturnMeeting() {
        // Datos de prueba
		UUID idOrganizer = UUID.randomUUID();
        User organizer = new User("organizer@example.com", "Organizer", "User", null, null, null, null, null, null, false, false, null);
        organizer.setID(idOrganizer);
        Meetings meeting = new Meetings("Team Meeting", LocalDate.parse("2021-12-01"), false, LocalTime.parse("10:00"), LocalTime.parse("11:00"),                 
        	    "Monthly sync", idOrganizer, UUID.randomUUID());

        when(meetingDao.save(any(Meetings.class))).thenReturn(meeting);

        // Llamada al servicio
        Meetings createdMeeting = meetingService.createMeeting(
            "Team Meeting", false, LocalDate.parse("2021-12-01") , 
            LocalTime.parse("10:00"), LocalTime.parse("11:00"), organizer.getName(), UUID.randomUUID(), UUID.randomUUID()
        );

        // Verificaciones
        assertNotNull(createdMeeting);
        assertEquals("Team Meeting", createdMeeting.getMeetingTitle());
        assertEquals(organizer.getID(), createdMeeting.getOrganizerId());
        verify(meetingDao, times(1)).save(any(Meetings.class));
    }

	@Test
	void getAvailableUsers_ShouldReturnNonBlockedUsers() {
		// Datos de prueba
		User user1 = new User("user1@example.com", "User", "One", null, null, null, null, null, null, false, false,
				null);
		User user2 = new User("user2@example.com", "User", "Two", null, null, null, null, null, null, false, false,
				null);

		when(userDao.findAllNotBlocked()).thenReturn(List.of(user1, user2));

		// Llamada al servicio
		List<User> availableUsers = meetingService.getAvailableUsers();

		// Verificaciones
		assertNotNull(availableUsers);
		assertEquals(2, availableUsers.size());
		verify(userDao, times(1)).findAllNotBlocked();
	}

	@Test
	void inviteUserToMeeting_ShouldSaveAndReturnInvitation() {
		// Datos de prueba
		User user = new User("user@example.com", "User", "Test", null, null, null, null, null, null, false, false,
				null);
		Meetings meeting = new Meetings("Project Kickoff", LocalDate.parse("2024-12-15"), false, LocalTime.parse("09:00"), LocalTime.parse("10:00"),				"", user.getID(), UUID.randomUUID());
		Invitations invitation = new Invitations(meeting, user, InvitationStatus.PENDIENTE, false, "");

		when(invitationDao.save(any(Invitations.class))).thenReturn(invitation);

		// Llamada al servicio
		Invitations createdInvitation = meetingService.inviteUserToMeeting(meeting, user, InvitationStatus.PENDIENTE);

		// Verificaciones
		assertNotNull(createdInvitation);
		assertEquals(InvitationStatus.PENDIENTE.toString(), createdInvitation.getInvitationStatus());
		assertEquals(meeting, createdInvitation.getMeeting());
		assertEquals(user, createdInvitation.getUser());
		verify(invitationDao, times(1)).save(any(Invitations.class));
	}

	@Test
	void getMeetingById_ShouldReturnMeetingIfExists() {
		// Datos de prueba
		UUID meetingId = UUID.randomUUID();
		Meetings meeting = new Meetings();
		meeting.setMeetingId(meetingId);

		when(((MeetingsDao) meetingDao).findById(meetingId)).thenReturn(Optional.of(meeting));

		// Llamada al servicio
		Optional<Meetings> result = meetingService.getMeetingById(meetingId);

		// Verificaciones
		assertTrue(result.isPresent());
		assertEquals(meetingId, result.get().getMeetingId());
		verify(meetingDao, times(1)).findById(meetingId);
	}

	@Test
	void getAttendeesForMeeting_ShouldReturnAcceptedUsers() {
		// Datos de prueba
		User user1 = new User();
		User user2 = new User();
		Invitations invitation1 = new Invitations(existingMeeting, user1, InvitationStatus.ACEPTADA, false, null);
		Invitations invitation2 = new Invitations(existingMeeting, user2, InvitationStatus.ACEPTADA, false, null);
		Invitations invitation3 = new Invitations(existingMeeting, new User(), InvitationStatus.RECHAZADA, false, null);

		List<Invitations> invitations = Arrays.asList(invitation1, invitation2, invitation3);

		// Configurar el mock
		when(invitationDao.findByMeetingId(existingMeeting.getMeetingId())).thenReturn(invitations);
		

		// Ejecutar el método
		List<UUID> attendees = meetingService.getAttendeesForMeeting(existingMeeting);

		// Verificar resultados
		assertEquals(2, attendees.size());
		assertTrue(attendees.contains(user1.getID()));
	    assertTrue(attendees.contains(user2.getID()));
	}

	@Test
	void testModifyMeetingSuccess() {
		// Arrange
		when(meetingDao.findById(existingMeeting.getMeetingId())).thenReturn(Optional.of(existingMeeting));
		when(meetingDao.findConflictingMeetings(updatedMeeting.getMeetingDate(), updatedMeeting.getMeetingStartTime(),
				updatedMeeting.getMeetingEndTime())).thenReturn(Collections.emptyList());

		// Act
		meetingService.modifyMeeting(existingMeeting.getMeetingId(), updatedMeeting);

		// Assert
		verify(meetingDao, times(1)).updateMeeting(existingMeeting, updatedMeeting);
	}

	@Test
	void testModifyMeetingMeetingNotFound() {
		// Arrange
		when(meetingDao.findById(existingMeeting.getMeetingId())).thenReturn(Optional.empty());

		// Act & Assert
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			meetingService.modifyMeeting(existingMeeting.getMeetingId(), updatedMeeting);
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertEquals("Reunión no encontrada", exception.getReason());
		verify(meetingDao, never()).updateMeeting(any(), any());
	}

	@Test
	void testModifyMeetingConflictOrganizer() {
		// Arrange
		UUID conflictingMeetingId = UUID.randomUUID();
		when(meetingDao.findById(existingMeeting.getMeetingId())).thenReturn(Optional.of(existingMeeting));
		when(meetingDao.findConflictingMeetings(updatedMeeting.getMeetingDate(), updatedMeeting.getMeetingStartTime(),
				updatedMeeting.getMeetingEndTime())).thenReturn(List.of(conflictingMeetingId));

		// Act & Assert
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			meetingService.modifyMeeting(existingMeeting.getMeetingId(), updatedMeeting);
		});

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertEquals("El organizador tiene una reunión en el nuevo tramo", exception.getReason());
		verify(meetingDao, never()).updateMeeting(any(), any());
	}

	@Test
	void testModifyMeetingConflictWithin24Hours() {
		// Arrange
		updatedMeeting.setMeetingDate(LocalDate.now());
		updatedMeeting.setMeetingStartTime(LocalTime.now().plusHours(23));
		when(meetingDao.findById(existingMeeting.getMeetingId())).thenReturn(Optional.of(existingMeeting));
		when(meetingDao.findConflictingMeetings(updatedMeeting.getMeetingDate(), updatedMeeting.getMeetingStartTime(),
				updatedMeeting.getMeetingEndTime())).thenReturn(Collections.emptyList());
		
		// Act & Assert
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			meetingService.modifyMeeting(existingMeeting.getMeetingId(), updatedMeeting);
		});

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertEquals("No se puede modificar una reunión con menos de 24 horas de antelación", exception.getReason());
		verify(meetingDao, never()).updateMeeting(any(), any());
	}
    //TDD cancelar reunión por organizador
    @Test //TDD
    void testCancelMeetingByOrganizer_Success() {
    	// Datos de prueba
        UUID meetingId = UUID.randomUUID();
        //UUID organizerId = UUID.randomUUID();
        Meetings meeting = new Meetings();
        meeting.setMeetingId(meetingId);
        
        // Configurar el mock para que devuelva la reunión
        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(meeting));

        // Ejecutar el método
        boolean result = meetingService.cancelMeetingByOrganizer(meetingId);

        // Verificar comportamiento y resultado
        assertTrue(result);
        verify(meetingDao, times(1)).findById(meetingId);
        verify(meetingDao, times(1)).delete(meeting);
    }
    
    //TDD cancelar reunión por organizador
    @Test 
    void testCancelMeetingByOrganizer_MeetingNotFound() {
    	// Datos de prueba
        UUID meetingId = UUID.randomUUID();
        //UUID organizerId = UUID.randomUUID();
        Meetings meeting = new Meetings();
        meeting.setMeetingId(meetingId);
        
        // Configurar el mock para que no encuentre la reunión
        when(meetingDao.findById(meetingId)).thenReturn(Optional.empty());

        // Ejecutar el método y verificar excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            meetingService.cancelMeetingByOrganizer(meetingId)
        );
        assertEquals("Reunión no encontrada", exception.getMessage());

        // Verificar comportamiento
        verify(meetingDao, times(1)).findById(meetingId);
        verify(meetingDao, never()).delete(any(Meetings.class));
    }
    
    //TDD cancelar reunión, ahora automágico
    @Test
    void testCancelMeetingIfAllInvitationsRejected_Success() {
    	
    	UUID meetingId = UUID.randomUUID();
    	Meetings meeting = new Meetings();
    	User excludedUser = new User();
    	
    	meeting.setMeetingId(meetingId);
    	excludedUser.setID(UUID.randomUUID());
        
    	List<Invitations> invitations;
        
    	Invitations invitation1 = new Invitations();
    	Invitations invitation2 = new Invitations();
    	Invitations invitation3 = new Invitations();
        
    	invitation1.setInvitationStatus(InvitationStatus.RECHAZADA);
        invitation2.setInvitationStatus(InvitationStatus.RECHAZADA);
        invitation3.setInvitationStatus(InvitationStatus.PENDIENTE);
        invitation3.setUser(excludedUser);
        
        invitation1.setMeeting(meeting);
        invitation2.setMeeting(meeting);
        
        invitations = List.of(invitation1, invitation2);
        
        // Configurar mock para devolver las invitaciones y la reunión
        when(invitationDao.findByMeetingIdAndInvitationIdNot(meetingId, excludedUser.getID()))
        .thenReturn(invitations);
        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(meeting));

        // Ejecutar el método
        boolean result = meetingService.cancelMeetingIfAllInvitationsRejected(meetingId, excludedUser.getID());

        // Verificar comportamiento y resultado
        assertTrue(result); //allRejected = true
        verify(meetingDao, times(1)).delete(meeting);
    }
    
    //TDD cancelar reunión, ahora automágico
    @Test
    void testCancelMeetingIfAllInvitationsRejected_NotAllRejected() {
        UUID meetingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Meetings meeting = new Meetings();
        meeting.setMeetingId(meetingId);
        
        User excludedUser = new User();
        excludedUser.setID(UUID.randomUUID());

        Invitations invitation1 = new Invitations();
        Invitations invitation2 = new Invitations();
        Invitations invitation3 = new Invitations();

        invitation1.setInvitationStatus(InvitationStatus.RECHAZADA);
        invitation2.setInvitationStatus(InvitationStatus.ACEPTADA);
        invitation3.setInvitationStatus(InvitationStatus.RECHAZADA);
        invitation3.setUser(new User());

        invitation1.setMeeting(meeting);
        invitation2.setMeeting(meeting);

        List<Invitations> invitations = List.of(invitation1, invitation2);

        // Configurar mock para devolver las invitaciones y la reunión
        when(invitationDao.findByMeetingIdAndInvitationIdNot(meetingId, excludedUser.getID()))
        .thenReturn(invitations);

        // Ejecutar el método
        boolean result = meetingService.cancelMeetingIfAllInvitationsRejected(meetingId, excludedUser.getID());

        // Verificar comportamiento y resultado
        assertFalse(result); //allRejected = false, tenemos una aceptada ^
        verify(meetingDao, never()).delete(meeting); //no se borra
    }
}
