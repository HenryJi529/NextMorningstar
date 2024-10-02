package com.morningstar.practice.lib;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AiTest {

    @Autowired
    private ChatClient chatClient;

    @Test
    void test() {
        String response = chatClient.prompt("说相声的于谦的父亲是干什么的?").call().content();
        System.out.println(response);
    }
}
