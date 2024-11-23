package com.team1.sgart.backend.http;

import com.team1.sgart.backend.model.InvitationsDTO;
import com.team1.sgart.backend.model.MeetingsDTO;
import com.team1.sgart.backend.services.CalendarService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminCalendarTest {

    @InjectMocks
    private AdminCalendarController controller;

    @Mock
    private CalendarService calendarService;

    public AdminCalendarTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllMeetings() {
        // Arrange
        MeetingsDTO mockMeeting = new MeetingsDTO(UUID.randomUUID(), "Test Meeting", false, LocalDate.parse("2024-12-15"),  LocalTime.parse("09:00"), LocalTime.parse("10:00"), "", UUID.randomUUID(), "", "");
        when(calendarService.loadMeetings()).thenReturn(Collections.singletonList(mockMeeting));

        // Act
        ResponseEntity<List<MeetingsDTO>> response = controller.getAllMeetings();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Meeting", response.getBody().get(0).getMeetingTitle());
        verify(calendarService, times(1)).loadMeetings();
    }

    @Test
    public void testGetInvitees_ValidMeetingId() {
        // Arrange
        UUID meetingId = UUID.randomUUID();
        InvitationsDTO mockInvitation = new InvitationsDTO(1, meetingId, UUID.randomUUID(), "John Doe", "Accepted", true, null);
        when(calendarService.getDetailedInvitationsByMeetingId(meetingId)).thenReturn(Collections.singletonList(mockInvitation));

        Map<String, UUID> payload = new HashMap<>();
        payload.put("meetingId", meetingId);

        // Act
        ResponseEntity<List<InvitationsDTO>> response = controller.getInvitees(payload);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getUserName());
        verify(calendarService, times(1)).getDetailedInvitationsByMeetingId(meetingId);
    }

    @Test
    public void testGetInvitees_InvalidMeetingId() {
        // Arrange
        Map<String, UUID> payload = new HashMap<>();

        // Act
        ResponseEntity<List<InvitationsDTO>> response = controller.getInvitees(payload);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        verify(calendarService, never()).getDetailedInvitationsByMeetingId(any());
    }

    @Test
    public void testLoadMeetings_ServiceLayer() {
        // Arrange
        UUID meetingId = UUID.randomUUID();
        MeetingsDTO mockMeeting = new MeetingsDTO(
            meetingId, 
            "Team Meeting", // Cambiado el título para que coincida con el assert
            false, 
            LocalDate.parse("2024-12-15"), 
            LocalTime.parse("09:00"), 
            LocalTime.parse("10:00"), 
            "", 
            UUID.randomUUID(), 
            "", 
            ""
        );

        when(calendarService.loadMeetings()).thenReturn(Collections.singletonList(mockMeeting));

        // Act
        List<MeetingsDTO> meetings = calendarService.loadMeetings();

        // Assert
        assertNotNull(meetings);
        assertEquals(1, meetings.size());
        assertEquals(meetingId, meetings.get(0).getMeetingId());
        assertEquals("Team Meeting", meetings.get(0).getMeetingTitle()); // Asegúrate de que coincida el título
    }


    @Test
    public void testGetDetailedInvitationsByMeetingId_ServiceLayer() {
        // Arrange
        UUID meetingId = UUID.randomUUID();
        Object[] mockData = new Object[]{1, meetingId.toString(), UUID.randomUUID().toString(), "John", "Doe", "Accepted", true, null};
        List<Object[]> mockResults = Collections.singletonList(mockData);

        when(calendarService.getDetailedInvitationsByMeetingId(meetingId)).thenReturn(Collections.singletonList(new InvitationsDTO(1, meetingId, UUID.randomUUID(), "John Doe", "Accepted", true, null)));

        // Act
        List<InvitationsDTO> invitations = calendarService.getDetailedInvitationsByMeetingId(meetingId);

        // Assert
        assertNotNull(invitations);
        assertEquals(1, invitations.size());
        assertEquals("John Doe", invitations.get(0).getUserName());
    }
}