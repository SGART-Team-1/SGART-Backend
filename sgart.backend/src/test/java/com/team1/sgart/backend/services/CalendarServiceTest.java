package com.team1.sgart.backend.services;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team1.sgart.backend.dao.InvitationsDao;
import com.team1.sgart.backend.dao.MeetingsDao;
import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.model.Meetings;
import com.team1.sgart.backend.model.MeetingsDTO;
import com.team1.sgart.backend.model.Invitations;

@ExtendWith(MockitoExtension.class)
public class CalendarServiceTest {

    @Mock
    private MeetingsDao meetingsDao;

    @Mock
    private InvitationsDao invitationsDao;
    
    @Mock
    private UserDao userDao;
    
    @Mock
    private LocationsService locationsService;

    @InjectMocks
    private CalendarService calendarService;

    private Meetings mockMeeting;
    private UUID meetingId;
    private UUID userId;
    private UUID locationId;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
        userId = UUID.randomUUID();
        locationId = UUID.randomUUID();

        mockMeeting = new Meetings();
        mockMeeting.setMeetingId(meetingId);
        mockMeeting.setMeetingTitle("Test Meeting");
        mockMeeting.setMeetingAllDay(false);
        mockMeeting.setMeetingStartTime(LocalTime.of(9, 0));
        mockMeeting.setMeetingEndTime(LocalTime.of(10, 0));
        mockMeeting.setOrganizerId(userId);
        mockMeeting.setMeetingDate(LocalDate.now());
        mockMeeting.setLocationId(locationId);
        mockMeeting.setMeetingObservations("Test observations");
    }

    @Test
    void loadMeetings_ShouldReturnMeetingsList() {
        // Arrange
        UUID meetingId = UUID.randomUUID();
        UUID organizerId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();

        Meetings mockMeeting = new Meetings();
        mockMeeting.setMeetingId(meetingId);
        mockMeeting.setMeetingTitle("Test Meeting");
        mockMeeting.setMeetingAllDay(false);
        mockMeeting.setMeetingStartTime(LocalTime.of(10, 0));
        mockMeeting.setMeetingEndTime(LocalTime.of(11, 0));
        mockMeeting.setMeetingDate(LocalDate.now());
        mockMeeting.setMeetingObservations("Test Observations");
        mockMeeting.setOrganizerId(organizerId);
        mockMeeting.setLocationId(locationId);

        List<Meetings> mockMeetings = List.of(mockMeeting);

        when(meetingsDao.findAll()).thenReturn(mockMeetings);
        when(userDao.findUserFullNameById(organizerId)).thenReturn("Test Organizer");
        when(locationsService.getLocationById(locationId)).thenReturn("Test Location");

        // Act
        List<MeetingsDTO> result = calendarService.loadMeetings();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MeetingsDTO resultDTO = result.get(0);
        assertEquals(meetingId, resultDTO.getMeetingId());
        assertEquals("Test Meeting", resultDTO.getMeetingTitle());
        assertEquals("Test Organizer", resultDTO.getOrganizerName());
        assertEquals("Test Location", resultDTO.getLocationName());
        verify(meetingsDao).findAll();
        verify(userDao).findUserFullNameById(organizerId);
        verify(locationsService).getLocationById(locationId);
    }

    @Test
    void getMeetingsByUserId_ShouldReturnUserMeetings() {
        // Arrange
        Invitations mockInvitation = new Invitations();
        mockInvitation.setMeeting(mockMeeting);
        
        when(invitationsDao.findByUserId(userId)).thenReturn(List.of(mockInvitation));
        when(meetingsDao.findAllById(List.of(meetingId))).thenReturn(List.of(mockMeeting));
        when(locationsService.getLocationById(locationId)).thenReturn("Test Location");

        // Act
        List<MeetingsDTO> result = calendarService.getMeetingsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MeetingsDTO resultDTO = result.get(0);
        assertEquals(meetingId, resultDTO.getMeetingId());
        assertEquals("Test Meeting", resultDTO.getMeetingTitle());
        assertEquals("Test Location", resultDTO.getLocationName());
        verify(invitationsDao).findByUserId(userId);
        verify(meetingsDao).findAllById(anyList());
        verify(locationsService).getLocationById(locationId);
    }

    @Test
    void getMeetingsByUserId_WithNoMeetings_ShouldReturnEmptyList() {
        // Arrange
        when(invitationsDao.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<MeetingsDTO> result = calendarService.getMeetingsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(invitationsDao).findByUserId(userId);
        verify(meetingsDao).findAllById(Collections.emptyList());
    }
}