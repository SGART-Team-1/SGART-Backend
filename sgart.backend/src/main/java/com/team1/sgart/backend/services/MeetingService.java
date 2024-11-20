package com.team1.sgart.backend.services;

import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.dao.InvitationsDao;
import com.team1.sgart.backend.dao.MeetingsDao;
import com.team1.sgart.backend.model.Meetings;
import com.team1.sgart.backend.model.User;
import com.team1.sgart.backend.model.InvitationStatus;
import com.team1.sgart.backend.model.Invitations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MeetingsDao meetingDao;

    @Autowired
    private InvitationsDao invitationDao;

    // Método para crear la reunión
    public Meetings createMeeting(String meetingTitle, boolean meetingAllDay, LocalDate meetingDate, LocalTime meetingStartTime,
                                  LocalTime meetingEndTime, String observations, UUID organizerId, UUID locationId) {
        Meetings meeting = new Meetings(meetingTitle, meetingDate, meetingAllDay, meetingStartTime, meetingEndTime, observations, organizerId, locationId);
        return meetingDao.save(meeting);
    }

    // Método para obtener todos los usuarios habilitados
    public List<User> getAvailableUsers() {
        return userDao.findAllNotBlocked(); // Solo los usuarios no bloqueados
    }

    // Método para invitar a un usuario a una reunión
    public Invitations inviteUserToMeeting(Meetings meeting, User user, InvitationStatus status) {
        Invitations invitation = new Invitations( meeting, user, status, false, null);
        return invitationDao.save(invitation);
    }

    // Obtener una reunión por su ID
    public Optional<Meetings> getMeetingById(UUID meetingId) {
        return meetingDao.findById(meetingId);
    }

    // Obtener asistentes de una reunión por su ID
    public List<UUID> getAttendeesForMeeting(Meetings meeting) {
        List<Invitations> invitations = invitationDao.findByMeetingId(meeting.getMeetingId());

        // Filtramos aquellas con estado ACEPTADA y devolvemos los usuarios
        return invitations.stream()
                .filter(invitation -> invitation.getInvitationStatus() == InvitationStatus.ACEPTADA)
                .map(invitation -> invitation.getUser().getID())
                .collect(Collectors.toList());
    }

    // Obtener detalles de las invitaciones por ID de reunión
    public List<Object[]> getDetailedInvitationsForMeeting(UUID meetingId) {
        return invitationDao.findDetailedInvitationsByMeetingId(meetingId);
    }
    
    // Método para cancelar una reunión MANUAL por organizador
    public boolean cancelMeetingByOrganizer(UUID meetingId, UUID organizerId) {
        // Recuperamos la reunión por su ID
        Optional<Meetings> meetingOpt = meetingDao.findById(meetingId);
        if (meetingOpt.isEmpty()) {
            throw new RuntimeException("Reunión no encontrada");
        }

        Meetings meeting = meetingOpt.get();
        meetingDao.delete(meeting); //cancelada = eliminada 

        return true;
    }

    
}
