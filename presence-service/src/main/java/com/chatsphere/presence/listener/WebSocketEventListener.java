package com.chatsphere.presence.listener;

import com.chatsphere.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            try {
                UUID userId = UUID.fromString(principal.getName());
                presenceService.setOnline(userId);
                log.info("WebSocket Session Connected for user: {}", userId);
            } catch (IllegalArgumentException e) {
                log.error("Failed to parse user UUID from WebSocket connect principal name: {}", principal.getName());
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            try {
                UUID userId = UUID.fromString(principal.getName());
                presenceService.setOffline(userId);
                log.info("WebSocket Session Disconnected for user: {}", userId);
            } catch (IllegalArgumentException e) {
                log.error("Failed to parse user UUID from WebSocket disconnect principal name: {}", principal.getName());
            }
        }
    }
}
