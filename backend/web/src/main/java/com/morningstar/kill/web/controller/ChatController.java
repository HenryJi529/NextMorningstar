package com.morningstar.kill.web.controller;

import com.morningstar.kill.websocket.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 广播发送消息
     */
    @MessageMapping("/sendToAll")
    @SendTo("/topic/notice")
    public ChatMessage sendToAll(Principal principal, @Payload String message) {
        return ChatMessage.builder().sender(principal.getName()).content(message).build();
    }

    /**
     * 点对点发送消息
     *
     * @param principal 当前用户
     * @param receiver  接收消息的用户
     * @param message   消息内容
     */
    @MessageMapping("/sendToOne/{receiver}")
    public void sendToUser(Principal principal, @DestinationVariable String receiver, @Payload String message) {
        // 发送消息给指定用户 /user/{receiver}/queue/greeting
        messagingTemplate.convertAndSendToUser(
                receiver,
                "/queue/greeting",
                ChatMessage.builder().sender(principal.getName()).content(message).build()
        );
    }
}
