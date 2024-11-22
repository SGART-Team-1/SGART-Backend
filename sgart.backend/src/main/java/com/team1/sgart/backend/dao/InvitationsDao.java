package com.team1.sgart.backend.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.team1.sgart.backend.model.Invitations;

import jakarta.transaction.Transactional;

@Repository
public interface InvitationsDao extends JpaRepository<Invitations, Integer> {
    @Query(value = """
            SELECT i.invitation_id, i.meeting_id, i.user_id, u.user_name, u.user_lastName,
                   i.invitation_status, i.user_attendance, i.rejection_reason
            FROM SGART_InvitationsTable i
            JOIN SGART_UsersTable u ON i.user_id = u.user_id
            WHERE i.meeting_id = :meetingId
            """, nativeQuery = true)
    List<Object[]> findDetailedInvitationsByMeetingId(@Param("meetingId") UUID meetingId);

    @Query("SELECT i FROM Invitations i WHERE i.meeting.meetingId = :meetingId")
    List<Invitations> findByMeetingId(@Param("meetingId") UUID meetingId);
    
    @Query("SELECT i FROM Invitations i WHERE i.meeting.meetingId = :meetingId AND i.invitation_id <> :excludedInvitationId")
    List<Invitations> findByMeetingIdAndInvitationIdNot(UUID meetingId, UUID excludedInvitationId);

    @Query("SELECT i FROM Invitations i WHERE i.user.id = :userId")
    List<Invitations> findByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT COUNT(*) FROM SGART_InvitationsTable " +
           "WHERE MEETING_ID = :meetingId AND USER_ID = :userId", 
           nativeQuery = true)
    int countByMeetingIdAndUserId(
        @Param("meetingId") UUID meetingId, 
        @Param("userId") UUID userId
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE SGART_InvitationsTable " +
           "SET INVITATION_STATUS = :newStatus, REJECTION_REASON = :comment " +
           "WHERE MEETING_ID = :meetingId AND USER_ID = :userId", 
           nativeQuery = true)
    int updateInvitationStatus(
        @Param("meetingId") UUID meetingId, 
        @Param("userId") UUID userId, 
        @Param("newStatus") String newStatus,
        @Param("comment") String comment
    );
}