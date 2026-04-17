package egovframework.com.jwt;

import egovframework.com.cmm.service.EgovProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OAuthLinkTokenUtil {

    private static final long LINK_TOKEN_VALIDITY_MS = 5 * 60 * 1000; // 5 minutes

    private final Environment environment;
    private String secretKeyString;

    public OAuthLinkTokenUtil(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    private void init() {
        String secretKey = environment.getProperty("Globals.jwt.secret");
        if (secretKey == null || secretKey.trim().isEmpty()) {
            secretKey = EgovProperties.getProperty("Globals.jwt.secret");
        }
        this.secretKeyString = secretKey;
    }

    private SecretKey getSecretKey() {
        if (secretKeyString == null || secretKeyString.trim().isEmpty() ||
                "99".equals(secretKeyString) || " EXCEPTION OCCURRED".equals(secretKeyString)) {
            throw new IllegalArgumentException("JWT secret key is not configured");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes after Base64 decoding");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createLinkToken(OAuthLinkTokenClaims c) {
        if (c == null || c.getEmail() == null || c.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("email is required");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "OAuthLink");
        claims.put("email", c.getEmail());
        claims.put("userSe", c.getUserSe());
        claims.put("oauthService", c.getOauthService());
        claims.put("oauthGb", c.getOauthGb());
        claims.put("oauthAuthId", c.getOauthAuthId());
        claims.put("state", c.getState());

        SecretKey key = getSecretKey();
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject("OAuthLink")
                .issuedAt(new Date(now))
                .expiration(new Date(now + LINK_TOKEN_VALIDITY_MS))
                .signWith(key)
                .compact();
    }

    public OAuthLinkTokenClaims parseLinkToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("token is required");
        }
        try {
            SecretKey key = getSecretKey();
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object type = claims.get("type");
            if (type == null || !"OAuthLink".equals(String.valueOf(type))) {
                throw new IllegalArgumentException("Invalid token type");
            }

            OAuthLinkTokenClaims c = new OAuthLinkTokenClaims();
            c.setEmail(asString(claims.get("email")));
            c.setUserSe(asString(claims.get("userSe")));
            c.setOauthService(asString(claims.get("oauthService")));
            c.setOauthGb(asString(claims.get("oauthGb")));
            c.setOauthAuthId(asString(claims.get("oauthAuthId")));
            c.setState(asString(claims.get("state")));
            return c;
        } catch (JwtException e) {
            log.warn("Invalid link token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid link token");
        }
    }

    private String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}

