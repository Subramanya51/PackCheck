package com.hotel.packcheck.service;

import com.hotel.packcheck.dto.CartWebSocketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartWebSocketService {

    private static final String CART_TOPIC = "/topic/carts";

    private final SimpMessagingTemplate messagingTemplate;

    public void publishCartStatus(
            CartWebSocketStatus status) {

        messagingTemplate.convertAndSend(
                CART_TOPIC,
                status
        );
    }
}