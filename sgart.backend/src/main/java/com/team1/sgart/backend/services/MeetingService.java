package com.team1.sgart.backend.services;

import com.team1.sgart.backend.dao.UserDao;
import com.team1.sgart.backend.dao.InvitationsDao;
import com.team1.sgart.backend.dao.MeetingsDao;
import com.team1.sgart.backend.model.Meetings;
import com.team1.sgart.backend.model.User;
import com.team1.sgart.backend.model.InvitationStatus;
import com.team1.sgart.backend.model.Invitations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private UserDao userDao;
    private MeetingsDao meetingsDao;
    private InvitationsDao invitationsDao;
    
    @Autowired
	public MeetingService(UserDao userDao, MeetingsDao meetingsDao, InvitationsDao invitationsDao) {
		this.userDao = userDao;
		this.meetingsDao = meetingsDao;
		this.invitationsDao = invitationsDao;
	}

    // Método para crear la reunión
    public Meetings createMeeting(String meetingTitle, boolean meetingAllDay, LocalDate meetingDate, LocalTime meetingStartTime,
                                  LocalTime meetingEndTime, String observations, UUID organizerId, UUID locationId) {
        Meetings meeting = new Meetings(meetingTitle, meetingDate, meetingAllDay, meetingStartTime, meetingEndTime, observations, organizerId, locationId);
        return meetingsDao.save(meeting);
    }

    // Método para obtener todos los usuarios habilitados
    public List<User> getAvailableUsers() {
        return userDao.findAllNotBlocked(); // Solo los usuarios no bloqueados
    }

    // Método para invitar a un usuario a una reunión
    public Invitations inviteUserToMeeting(Meetings meeting, User user, InvitationStatus status) {
        Invitations invitation = new Invitations(meeting, user, status, false, "");

        return invitationsDao.save(invitation);
    }

    // Obtener una reunión por su ID
    public Optional<Meetings> getMeetingById(UUID meetingId) {
        return meetingsDao.findById(meetingId);
    }

    // Obtener asistentes de una reunión por su ID
    public List<UUID> getAttendeesForMeeting(Meetings meeting) {

        List<Invitations> invitations = invitationsDao.findByMeetingId(meeting.getMeetingId());

        // Filtramos aquellas con estado ACEPTADA y devolvemos los usuarios
        return invitations.stream()
                .filter(invitation -> InvitationStatus.valueOf(invitation.getInvitationStatus()) == InvitationStatus.ACEPTADA)
                .map(invitation -> invitation.getUser().getID())
                .collect(Collectors.toList());
    }

    // Obtener detalles de las invitaciones por ID de reunión
    public List<Object[]> getDetailedInvitationsForMeeting(UUID meetingId) {
        return invitationsDao.findDetailedInvitationsByMeetingId(meetingId);
    }
    
    // Modificar las reuniones
    public void modifyMeeting(UUID idMeeting, Meetings updatedMeeting) {
		Meetings meeting;
		//Comprobamos si la reunión existe
		if (!meetingsDao.findById(idMeeting).isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reunión no encontrada");
		}
		
		else {
			 meeting = meetingsDao.findById(idMeeting).get();
		}	
		//Se revisan las reuniones del organizador, si tiene una reunión en el nuevo tramo, error de no permitido
		List<UUID> conflictingMeetings = meetingsDao.findConflictingMeetings(updatedMeeting.getMeetingDate(),
				updatedMeeting.getMeetingStartTime(), updatedMeeting.getMeetingEndTime());
		if (!conflictingMeetings.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El organizador tiene una reunión en el nuevo tramo");
		}
		else if(isWithin24Hours(LocalTime.now(), updatedMeeting.getMeetingStartTime(), LocalDate.now(), updatedMeeting.getMeetingDate())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede modificar una reunión con menos de 24 horas de antelación");
		}
		else 
		{
			// Se revisan las reuniones de los invitados, si tienen una reunión en el nuevo tramo, se elimina al invitado de la reunión
			List<UUID> attendees = invitationsDao.findUserIdsByMeetingId(idMeeting); //Crear método para extraer de la tabla de invitaciones
			for (UUID attendee : attendees) {
				for (UUID idMeetingSearch : conflictingMeetings) {
					// Si el usuario tiene una reunión en el nuevo tramo, se elimina de la reunión
					if (invitationsDao.checkUserHaveMeeting(attendee, idMeetingSearch) > 0) {
						// Eliminar al invitado de la tabla de reuniones
						invitationsDao.deleteByMeetingIdAndUserId(attendee, idMeetingSearch);
					}
				}
				conflictingMeetings = meetingsDao.findConflictingMeetings(updatedMeeting.getMeetingDate(),
						updatedMeeting.getMeetingStartTime(), updatedMeeting.getMeetingEndTime());
				if (!conflictingMeetings.isEmpty()) {
					//Eliminar al invitado de la tabla de reunioines
				}
			}
		}
		
		meetingsDao.updateMeeting(meeting, updatedMeeting);
	}

	// Método para cancelar una reunión MANUAL por organizador
	public boolean cancelMeetingByOrganizer(UUID meetingId) {
		// Recuperamos la reunión por su ID
		Optional<Meetings> meetingOpt = meetingDao.findById(meetingId);
		if (meetingOpt.isEmpty()) {
			throw new RuntimeException("Reunión no encontrada");
		}

		Meetings meeting = meetingOpt.get();
		meetingDao.delete(meeting); // cancelada = eliminada

		return true;
	}

	// Método para cancelar una reunión si todas las invitaciones están rechazadas
	public boolean cancelMeetingIfAllInvitationsRejected(UUID meetingId, UUID excludedInvitationId) {
		
		// Recuperamos todas las invitaciones EXCEPTO la que se está procesando por si no se ha guardado Rechazada aún
		List<Invitations> invitations = invitationDao.findByMeetingIdAndInvitationIdNot(meetingId, excludedInvitationId);




		if (invitations.isEmpty()) {
			throw new RuntimeException("No se encontraron invitaciones para la reunión");
		}

		// Verificamos que todas las invitaciones están rechazadas
		boolean allRejected = invitations.stream()
				.allMatch(invitation -> invitation.getInvitationStatus().equals(InvitationStatus.RECHAZADA));

		if (allRejected) {
// ------------------------------------------------------------------------------------Esto es igual que cancelMeetingByOrganizer se podría juntar :P
			Optional<Meetings> meetingOpt = meetingDao.findById(meetingId);
			if (meetingOpt.isEmpty()) {
				throw new RuntimeException("Reunión no encontrada");
			}

			Meetings meeting = meetingOpt.get();
			meetingDao.delete(meeting);
			return true;
			//--------------------------------------------------------------------------
		}

		return false;
	}

public boolean isWithin24Hours(LocalTime nowTime, LocalTime targetTime, LocalDate nowDate, LocalDate targetDate) {
        LocalDateTime nowDateTime = LocalDateTime.of(nowDate, nowTime);
        LocalDateTime targetDateTime = LocalDateTime.of(targetDate, targetTime);
        
        Duration duration = Duration.between(nowDateTime, targetDateTime);
        long hoursDifference = duration.toHours();
        
        return hoursDifference < 24;
    }
}
