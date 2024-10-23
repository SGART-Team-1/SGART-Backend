package com.team1.sgart.backend.model;


public class TwoFactorAuth {
    private String mail;
    private String code;

    // Constructor vacío
    public TwoFactorAuth() {}

    // Getters y Setters
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
