package com.morningstar.kill.pojo.bo;

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
