package com.hotel.lostfound.notification.web;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    StompAuthChannelInterceptor(JwtDecoder jwtDecoder, JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }
        String token = bearer(accessor.getFirstNativeHeader("Authorization"));
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("STOMP CONNECT requires a Bearer token");
        }
        Jwt jwt = jwtDecoder.decode(token);
        var authentication = jwtAuthenticationConverter.convert(jwt);
        if (authentication == null) {
            throw new IllegalArgumentException("STOMP CONNECT token is not a staff principal");
        }
        accessor.setUser(new UsernamePasswordAuthenticationToken(
                authentication.getName(), token, authentication.getAuthorities()));
        return message;
    }

    private static String bearer(String header) {
        if (header == null) {
            return null;
        }
        return header.regionMatches(true, 0, "Bearer ", 0, 7) ? header.substring(7).trim() : header.trim();
    }
}
