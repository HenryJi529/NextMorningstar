package com.morningstar.kill.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class  WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sender = Objects.requireNonNull(headerAccessor.getUser()).getName();
        if (sender != null) {
            log.info("user disconnected: {}", sender);
            messagingTemplate.convertAndSend("/topic/notice", "用户下线");
        }
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sender = Objects.requireNonNull(headerAccessor.getUser()).getName();
        if (sender != null) {
            log.info("user connected: {}", sender);
            messagingTemplate.convertAndSend("/topic/notice", "用户断连");
        }
    }

}
