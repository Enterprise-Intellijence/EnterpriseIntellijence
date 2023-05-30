package com.enterpriseintellijence.enterpriseintellijence.data.services;

import jakarta.mail.Message;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImp implements EmailService {
    JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String text) {
        javaMailSender.send(mimeMessage -> {
            mimeMessage.setRecipients(Message.RecipientType.TO, to);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(text);
        });
    }
}
