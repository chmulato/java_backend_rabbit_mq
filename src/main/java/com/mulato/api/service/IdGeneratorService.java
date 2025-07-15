package com.mulato.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class IdGeneratorService {
    
    @Value("${app.search.id-length:8}")
    private int idLength;
    
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom random = new SecureRandom();
    
    public String generateId() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < idLength; i++) {
            builder.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return builder.toString();
    }
}
