package com.pharm.pharmavigil_platform.config.websocket;

import java.security.Principal;

/**
 * Name is the authenticated user's id (as a string), matching what
 * SimpMessagingTemplate.convertAndSendToUser(...) is called with on the publishing side.
 */
public record StompPrincipal(String name) implements Principal {

    @Override
    public String getName() {
        return name;
    }
}
