package com.morningstar.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MailUtil {
    @Value("${spring.mail.username}")
    private String from;

    @Value("${morningstar.mail.admin}")
    private String admin;

    private final JavaMailSender mailSender;

    public void sendSimpleMail(String to, String subject, String content, String cc){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        if(cc != null){
            message.setCc(cc);
        }
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    public void sendSimpleMailCcAdmin(String to, String subject, String content){
        sendSimpleMail(to, subject, content, admin);
    }

    public void sendSimpleMail(String to, String subject, String content) {
        sendSimpleMail(to, subject, content, null);
    }

    public void sendSimpleMailToAdmin(String subject, String content) {
        sendSimpleMail(admin, subject, content);
    }

}
