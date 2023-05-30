package com.enterpriseintellijence.enterpriseintellijence.data.services;

public interface EmailService {
    public void sendEmail(String to, String subject, String text);
}
