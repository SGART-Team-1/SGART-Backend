package com.team1.sgart.backend.dao;

import com.team1.sgart.backend.model.Meetings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Repository
public interface MeetingsDao extends JpaRepository<Meetings, UUID> {


    @Query("SELECT m FROM Meetings m WHERE m.organizerId = :userId")
    List<Meetings> findByOrganizerId(@Param("userId") UUID userId);


	// Método para obtener una reunión por su ID
	@SuppressWarnings("null")
	Optional<Meetings> findById(UUID meetingId);

	// Método para editar una reunión

	default Meetings updateMeeting(Meetings meeting, Meetings updatedMeeting) {

		// Actualizar los campos
		actualizarCampo(meeting::setMeetingTitle, updatedMeeting.getMeetingTitle());
		actualizarCampo(meeting::setMeetingAllDay, updatedMeeting.isMeetingAllDay());
		actualizarCampo(meeting::setMeetingStartTime, updatedMeeting.getMeetingStartTime());
		actualizarCampo(meeting::setMeetingEndTime, updatedMeeting.getMeetingEndTime());
		actualizarCampo(meeting::setOrganizerId, updatedMeeting.getOrganizerId());
		actualizarCampo(meeting::setLocationId, updatedMeeting.getLocationId());
		actualizarCampo(meeting::setMeetingObservations, updatedMeeting.getMeetingObservations());
		// Guardar la reunión actualizada
		return save(meeting);
	}

	private <T> void actualizarCampo(Consumer<T> setter, T nuevoValor) {
		if (nuevoValor != null && !(nuevoValor instanceof String str && str.isEmpty())) {
			setter.accept(nuevoValor);
		}
	}

	@Query(value = """
			SELECT m.meeting_id 
			FROM SGART_MeetingsTable m
			WHERE m.meeting_date = :meetingDate
			AND (
				CAST(CAST(:meetingDate AS DATETIME) + CAST(m.meeting_start_time AS DATETIME) AS DATETIME) < :meetingEndTime
				AND CAST(CAST(:meetingDate AS DATETIME) + CAST(m.meeting_end_time AS DATETIME) AS DATETIME) > :meetingStartTime
			 );
				""", nativeQuery = true)
	List<UUID> findConflictingMeetings(@Param("meetingDate") LocalDate meetingDate,
			@Param("meetingStartTime") LocalTime meetingStartTime, @Param("meetingEndTime") LocalTime meetingEndTime);

	@Modifying
	@Query(value = "DELETE FROM SGART_InvitationsTable WHERE meeting_id = :meetingId", nativeQuery = true)
	void deleteInvitationsByMeetingId(@Param("meetingId") UUID meetingId);

	@Modifying
	@Query(value = "DELETE FROM SGART_MeetingsTable WHERE meeting_id = :meetingId", nativeQuery = true)
	void deleteMeetingById(@Param("meetingId") UUID meetingId);

}

