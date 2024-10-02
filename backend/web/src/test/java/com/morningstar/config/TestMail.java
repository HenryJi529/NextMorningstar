package com.morningstar.config;

import com.morningstar.util.MailUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestMail {
    private final MailUtil mailUtil;

    @Test
    public void testSendSimpleMailToAdmin() {
        mailUtil.sendSimpleMailToAdmin("发给admin", "纯文本内容");
    }
}
