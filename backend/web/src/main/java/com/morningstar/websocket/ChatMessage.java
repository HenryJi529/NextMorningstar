package com.morningstar.websocket;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String content;
}
