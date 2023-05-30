package com.enterpriseintellijence.enterpriseintellijence.data.services;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String text) {
        
    }
}
