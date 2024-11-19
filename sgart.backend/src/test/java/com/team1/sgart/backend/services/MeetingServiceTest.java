package com.team1.sgart.backend.services;




import com.team1.sgart.backend.dao.InvitationsDao;
import com.team1.sgart.backend.dao.MeetingsDao;
import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.model.Meetings;

import com.team1.sgart.backend.model.InvitationStatus;
import com.team1.sgart.backend.model.Invitations;
import com.team1.sgart.backend.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    

    @Autowired
    private MockMvc mockMvc;
    
    public MeetingServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMeeting_ShouldSaveAndReturnMeeting() {
        // Datos de prueba
        User organizer = new User("organizer@example.com", "Organizer", "User", null, null, null, null, null, null, false, false, null);
        LocalDate meetingDate = LocalDate.of(2021, 10, 15); // LocalDate.of(year, month, dayOfMonth este es el formato
        Meetings meeting = new Meetings("Team Meeting", meetingDate, false, LocalTime.of(10, 0), LocalTime.of(11, 0), "Reunión sin stakeholders",
        		organizer.getID(), UUID.randomUUID());

        when(meetingDao.save(any(Meetings.class))).thenReturn(meeting);

        // Llamada al servicio
        Meetings createdMeeting = meetingService.createMeeting("Team Meeting", false, meetingDate, LocalTime.of(10, 0), LocalTime.of(11, 0), "Reunión sin stakeholders",
        		organizer.getID(), UUID.randomUUID());

        // Verificaciones
        assertNotNull(createdMeeting);
        assertEquals("Team Meeting", createdMeeting.getMeetingTitle());
        assertEquals(organizer, createdMeeting.getOrganizerId());
        verify(meetingDao, times(1)).save(any(Meetings.class));
    }

    @Test
    void getAvailableUsers_ShouldReturnNonBlockedUsers() {
        // Datos de prueba
        User user1 = new User("user1@example.com", "User", "One", null, null, null, null, null, null, false, false, null);
        User user2 = new User("user2@example.com", "User", "Two", null, null, null, null, null, null, false, false, null);

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
        User user = new User("user@example.com", "User", "Test", null, null, null, null, null, null, false, false, null);
		
        Meetings meeting = new Meetings("Team Meeting", LocalDate.of(2021, 10, 15), false, LocalTime.of(10, 0),
				LocalTime.of(11, 0), "Reunión sin stakeholders", UUID.randomUUID(), UUID.randomUUID());

        Invitations invitation = new Invitations(meeting, user, InvitationStatus.PENDIENTE, false, null);

        when(invitationDao.save(any(Invitations.class))).thenReturn(invitation);

        // Llamada al servicio
        Invitations createdInvitation = meetingService.inviteUserToMeeting(meeting, user, InvitationStatus.PENDIENTE);

        // Verificaciones
        assertNotNull(createdInvitation);
        assertEquals(InvitationStatus.PENDIENTE, createdInvitation.getInvitationStatus());
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

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(meeting));

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
        Meetings meeting = new Meetings();
        User user1 = new User();
        User user2 = new User();
        Invitations invitation1 = new Invitations(meeting, user1, InvitationStatus.ACEPTADA, false, null);
        Invitations invitation2 = new Invitations(meeting, user2, InvitationStatus.ACEPTADA, false, null);
        Invitations invitation3 = new Invitations(meeting, new User(), InvitationStatus.RECHAZADA, false, null);

        List<Invitations> invitations = Arrays.asList(invitation1, invitation2, invitation3);

        // Configurar el mock
        when(invitationDao.findByMeetingId(meeting.getMeetingId())).thenReturn(invitations);

        // Ejecutar el método
        List<UUID> attendees = meetingService.getAttendeesForMeeting(meeting);

        // Verificar resultados
        assertEquals(2, attendees.size());
        assertEquals(user1, attendees.get(0));
        assertEquals(user2, attendees.get(1));
    }

}
