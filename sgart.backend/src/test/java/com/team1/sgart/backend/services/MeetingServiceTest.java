package com.team1.sgart.backend.services;


import com.team1.sgart.backend.dao.InvitationDAO;
import com.team1.sgart.backend.dao.MeetingDAO;
import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.model.Invitation;
import com.team1.sgart.backend.model.InvitationStatus;
import com.team1.sgart.backend.model.Meeting;
import com.team1.sgart.backend.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingDAO meetingDao;

    @Mock
    private UserDao userDao;

    @Mock
    private InvitationDAO invitationDao;

    @InjectMocks
    private MeetingService meetingService;

    @Test
    void createMeeting_ShouldSaveAndReturnMeeting() {
        // Datos de prueba
        User organizer = new User("organizer@example.com", "Organizer", "User", null, null, null, null, null, null, false, false, null);
        Meeting meeting = new Meeting("Team Meeting", false, 
            Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), organizer, "Room 101", "Monthly sync");

        when(meetingDao.save(any(Meeting.class))).thenReturn(meeting);

        // Llamada al servicio
        Meeting createdMeeting = meetingService.createMeeting(
            "Team Meeting", false, 
            Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), organizer, "Room 101", "Monthly sync"
        );

        // Verificaciones
        assertNotNull(createdMeeting);
        assertEquals("Team Meeting", createdMeeting.getTitle());
        assertEquals(organizer, createdMeeting.getOrganizer());
        verify(meetingDao, times(1)).save(any(Meeting.class));
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
        Meeting meeting = new Meeting("Project Kickoff", false, 
            Time.valueOf("09:00:00"), Time.valueOf("10:00:00"), user, "Room 202", "Kickoff meeting");
        Invitation invitation = new Invitation(meeting, user, InvitationStatus.PENDIENTE, false, null);

        when(invitationDao.save(any(Invitation.class))).thenReturn(invitation);

        // Llamada al servicio
        Invitation createdInvitation = meetingService.inviteUserToMeeting(meeting, user, InvitationStatus.PENDIENTE);

        // Verificaciones
        assertNotNull(createdInvitation);
        assertEquals(InvitationStatus.PENDIENTE, createdInvitation.getStatus());
        assertEquals(meeting, createdInvitation.getMeeting());
        assertEquals(user, createdInvitation.getUser());
        verify(invitationDao, times(1)).save(any(Invitation.class));
    }

    @Test
    void getMeetingById_ShouldReturnMeetingIfExists() {
        // Datos de prueba
        UUID meetingId = UUID.randomUUID();
        Meeting meeting = new Meeting();
        meeting.setId(meetingId);

        when(meetingDao.getMeetingById(meetingId)).thenReturn(Optional.of(meeting));

        // Llamada al servicio
        Optional<Meeting> result = meetingService.getMeetingById(meetingId);

        // Verificaciones
        assertTrue(result.isPresent());
        assertEquals(meetingId, result.get().getId());
        verify(meetingDao, times(1)).getMeetingById(meetingId);
    }
}