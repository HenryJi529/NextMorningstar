package com.morningstar.kill.websocket;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private String sender;
    private String content;
}
