package com.team1.sgart.backend.http;

import com.team1.sgart.backend.model.Meetings;
import com.team1.sgart.backend.model.User;
import com.team1.sgart.backend.model.InvitationStatus;
import com.team1.sgart.backend.model.Invitations;
import com.team1.sgart.backend.services.MeetingService;
import com.team1.sgart.backend.services.UserService;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserService userService;

    // Crear una reunión
    @PostMapping("/create")
    public ResponseEntity<Meetings> createMeeting(@RequestBody Meetings meeting) {
        Meetings createdMeeting = meetingService.createMeeting(meeting.getMeetingTitle(), meeting.isMeetingAllDay(), meeting.getMeetingDate(), 
        		meeting.getMeetingStartTime(), meeting.getMeetingEndTime(), meeting.getMeetingObservations(), meeting.getOrganizerId(), meeting.getLocationId());
        
        return ResponseEntity.ok(createdMeeting);
    }

    // Obtener usuarios habilitados para ser invitados
    @GetMapping("/available-users")
    public ResponseEntity<List<User>> getAvailableUsers() {
        List<User> availableUsers = meetingService.getAvailableUsers();
        return availableUsers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(availableUsers);
    }

    /*
    // Comprobar la disponibilidad de un usuario para una reunión
    @GetMapping("/check-availability/{userId}")
    public ResponseEntity<Boolean> checkUserAvailability(@PathVariable UUID userId, @RequestParam("startTime") LocalTime startTime, 
    		@RequestParam("endTime") LocalTime endTime) {
        Optional<User> userOpt = userService.getUserById(userId);  // Obtener el usuario con el UserService para no hacer otro método
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean isAvailable = meetingService.isUserAvailable(user, startTime, endTime);
            return ResponseEntity.ok(isAvailable);
        } else {
            return ResponseEntity.notFound().build(); // Si el usuario no se encuentra
        }
    }
*/

    // Invitar a un usuario a una reunión
    @PostMapping("/invite/{meetingId}/{userId}")
    public ResponseEntity<Invitations> inviteUserToMeeting(@PathVariable UUID meetingId, @PathVariable UUID userId) {
        Optional<Meetings> meetingOpt = meetingService.getMeetingById(meetingId);
        Optional<User> userOpt = userService.getUserById(userId);

        if (meetingOpt.isPresent() && userOpt.isPresent()) {
            Meetings meeting = meetingOpt.get();
            User user = userOpt.get();
            Invitations invitation = meetingService.inviteUserToMeeting(meeting, user, InvitationStatus.PENDIENTE);
            return ResponseEntity.ok(invitation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Obtener los asistentes a una reunión 
    @GetMapping("/{meetingId}/attendees")
    public List<UUID> getAttendees(@PathVariable("meetingId") UUID meetingId) {
        Meetings meeting = meetingService.getMeetingById(meetingId).orElseThrow(() -> new RuntimeException("ERROR: Reunión no encontrada"));
        return meetingService.getAttendeesForMeeting(meeting);
    }
    

    //Organizador cancela reunión
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> cancelMeeting(@PathVariable UUID meetingId) {
        try {
            meetingService.cancelMeetingByOrganizer(meetingId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).build();
        }
    }
    
    //Cancelar reunión si todas las invitaciones son rechazadas
    @PostMapping("/cancel/rejected")
    public ResponseEntity<String> cancelMeetingIfAllInvitationsRejected(
            @RequestParam UUID meetingId,
            @RequestParam UUID excludedUserId) {
        try {
            boolean result = meetingService.cancelMeetingIfAllInvitationsRejected(meetingId, excludedUserId);
            if (result) {
                return ResponseEntity.ok("Reunión cancelada debido a que todas las invitaciones fueron rechazadas");
            } else {
                return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                        .body("La reunión sigue adelante, hay invitaciones pendientes o aceptadas");
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(ex.getMessage());
        }
    }

    
}

