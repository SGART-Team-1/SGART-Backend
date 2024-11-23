package com.team1.sgart.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SGART_InvitationsTable")
public class Invitations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int invitation_id;

    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meetings meeting;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "invitation_status", nullable = false, length = 50)
    private String invitationStatus;

    @Column(name = "user_attendance", nullable = false)
    private boolean userAttendance;

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    // Constructor vacío
    public Invitations() {}

    // Constructor con parámetros
    public Invitations(Meetings meeting, User user, InvitationStatus invitationStatus, boolean userAttendance, String rejectionReason) {
        this.meeting = meeting;
        this.user = user;
        this.invitationStatus = invitationStatus.toString();
        this.userAttendance = userAttendance;
        this.rejectionReason = rejectionReason;
    }

    // Getters y setters

    public int getInvitationId() {
        return invitation_id;
    }

    public void setInvitationId(int invitationId) {
        this.invitation_id = invitationId;
    }

    public Meetings getMeeting() {
        return meeting;
    }

    public void setMeeting(Meetings meeting) {
        this.meeting = meeting;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public boolean isUserAttendance() {
        return userAttendance;
    }

    public void setUserAttendance(boolean userAttendance) {
        this.userAttendance = userAttendance;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
