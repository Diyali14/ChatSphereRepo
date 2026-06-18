package com.chatsphere.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptUpdateEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String messageId;
    private String clientMessageId;
    private String status;
    private String senderId;   // the user who sent the original message (to receive the receipt)
    private String receiverId; // the user who read/received it
}
