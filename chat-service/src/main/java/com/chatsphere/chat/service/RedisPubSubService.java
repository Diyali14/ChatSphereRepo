package com.chatsphere.chat.service;

import com.chatsphere.chat.dto.ReceiptUpdateEvent;
import com.chatsphere.common.event.MessageSentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPubSubService {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHANNEL_CHAT = "chatsphere:ws:chat";
    private static final String CHANNEL_RECEIPT = "chatsphere:ws:receipt";

    // Publish event to Redis PubSub
    public void publishChatMessage(MessageSentEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(CHANNEL_CHAT, json);
            log.info("Published message to Redis PubSub: {}", event.getMessageId());
        } catch (Exception e) {
            log.error("Failed to publish message to Redis PubSub", e);
        }
    }

    // Handle incoming messages from Redis PubSub
    public void receiveChatMessage(String messageJson) {
        try {
            MessageSentEvent event = objectMapper.readValue(messageJson, MessageSentEvent.class);
            log.info("Received message from Redis PubSub: {}. Routing to WS client: {}", event.getMessageId(), event.getReceiverId());
            
            // Push message to the recipient's private user queue: /user/{userId}/queue/messages
            messagingTemplate.convertAndSendToUser(
                    event.getReceiverId().toString(),
                    "/queue/messages",
                    event
            );
        } catch (Exception e) {
            log.error("Failed to route Redis PubSub message to WebSocket client", e);
        }
    }

    // Publish receipt to Redis PubSub
    public void publishReceipt(ReceiptUpdateEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(CHANNEL_RECEIPT, json);
            log.info("Published receipt to Redis PubSub: {}", event.getMessageId());
        } catch (Exception e) {
            log.error("Failed to publish receipt to Redis PubSub", e);
        }
    }

    // Handle incoming receipts from Redis PubSub
    public void receiveReceiptMessage(String receiptJson) {
        try {
            ReceiptUpdateEvent event = objectMapper.readValue(receiptJson, ReceiptUpdateEvent.class);
            log.info("Received receipt from Redis PubSub: {}. Routing to WS sender: {}", event.getMessageId(), event.getSenderId());
            
            // Push receipt to the sender's private user queue: /user/{userId}/queue/receipts
            messagingTemplate.convertAndSendToUser(
                    event.getSenderId(),
                    "/queue/receipts",
                    event
            );
        } catch (Exception e) {
            log.error("Failed to route Redis PubSub receipt to WebSocket client", e);
        }
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter chatListenerAdapter,
            MessageListenerAdapter receiptListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(chatListenerAdapter, new PatternTopic(CHANNEL_CHAT));
        container.addMessageListener(receiptListenerAdapter, new PatternTopic(CHANNEL_RECEIPT));
        return container;
    }

    @Bean
    public MessageListenerAdapter chatListenerAdapter(RedisPubSubService redisPubSubService) {
        return new MessageListenerAdapter(redisPubSubService, "receiveChatMessage");
    }

    @Bean
    public MessageListenerAdapter receiptListenerAdapter(RedisPubSubService redisPubSubService) {
        return new MessageListenerAdapter(redisPubSubService, "receiveReceiptMessage");
    }
}
