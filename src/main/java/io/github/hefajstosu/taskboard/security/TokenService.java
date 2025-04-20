package io.github.hefajstosu.taskboard.security;

import io.github.hefajstosu.taskboard.user.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {


    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public TokenService(JwtEncoder jwtEncoder, JwtProperties properties) {
        this.jwtEncoder = jwtEncoder;
		this.properties = properties;
    }

    public String issue(User user) { 
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("taskboard")
                    .issuedAt(now)
                .expiresAt(now.plus(properties.ttl()))
            .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getDisplayName())
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long ttlSeconds(){
        return properties.ttl().toSeconds();
    }
}
