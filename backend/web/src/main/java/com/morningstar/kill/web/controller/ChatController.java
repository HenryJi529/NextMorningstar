package com.morningstar.kill.web.controller;

import com.morningstar.common.pojo.bo.LoginUser;
import com.morningstar.kill.pojo.bo.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 广播发送消息
     */
    @MessageMapping("/sendToAll")
    public void sendToAll(ChatMessage chatMessage, Principal principal) {
        LoginUser loginUser = (LoginUser) (((Authentication) principal).getPrincipal());
        System.out.println(loginUser.getUser().getUsername());
        messagingTemplate.convertAndSend("/topic/kill.chat.all", chatMessage);
    }

    /**
     * 点对点发送消息
     */
    @MessageMapping("/sendToOne")
    public void sendToUser(ChatMessage chatMessage) {
        messagingTemplate.convertAndSend("/queue/kill.chat." + chatMessage.getReceiverId(), chatMessage);
    }
}
