package com.morningstar.util;

import com.morningstar.properties.SiteProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailUtil {
    private final SiteProperties siteProperties;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    public void sendSimpleEmail(String to, String subject, String content, String cc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        if (cc != null) {
            message.setCc(cc);
        }
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        PrintUtil.suppressSystemOut(() -> mailSender.send(message));
    }

    public void sendSimpleEmailCcAdmin(String to, String subject, String content) {
        sendSimpleEmail(to, subject, content, siteProperties.getAdminEmail());
    }

    public void sendSimpleEmail(String to, String subject, String content) {
        sendSimpleEmail(to, subject, content, null);
    }

    public void sendSimpleEmailToAdmin(String subject, String content) {
        sendSimpleEmail(siteProperties.getAdminEmail(), subject, content);
    }

}
