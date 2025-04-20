package io.github.hefajstosu.taskboard.security;

import org.springframework.boot.context.properties.ConfigurationProperties; 

import java.time.Duration;

// secret min 32 bytes!
@ConfigurationProperties("taskboard.jwt")
public record JwtProperties(String secret, Duration ttl) {
}
