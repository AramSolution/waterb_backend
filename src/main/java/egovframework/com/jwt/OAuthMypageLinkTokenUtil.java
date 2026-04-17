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

/**
 * MY PAGE에서 이미 로그인한 사용자가 SNS만 연결할 때 OAuth state에 실어 보내는 단기 JWT.
 * 브라우저 리다이렉트 콜백에서는 Authorization 헤더가 없으므로 ESNTL_ID를 state로 전달한다.
 */
@Slf4j
@Component
public class OAuthMypageLinkTokenUtil {

    private static final String CLAIM_TYPE = "OAuthMypageLink";
    private static final long VALIDITY_MS = 10 * 60 * 1000;

    private final Environment environment;
    private String secretKeyString;

    public OAuthMypageLinkTokenUtil(Environment environment) {
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
        if (secretKeyString == null || secretKeyString.trim().isEmpty()
                || "99".equals(secretKeyString) || " EXCEPTION OCCURRED".equals(secretKeyString)) {
            throw new IllegalArgumentException("JWT secret key is not configured");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes after Base64 decoding");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createMypageLinkToken(String esntlId, String userSe, String stateKey) {
        if (esntlId == null || esntlId.trim().isEmpty()) {
            throw new IllegalArgumentException("esntlId is required");
        }
        if (userSe == null || userSe.trim().isEmpty()) {
            throw new IllegalArgumentException("userSe is required");
        }
        if (stateKey == null || stateKey.trim().isEmpty()) {
            throw new IllegalArgumentException("stateKey is required");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", CLAIM_TYPE);
        claims.put("esntlId", esntlId.trim());
        claims.put("userSe", userSe.trim());
        claims.put("stateKey", stateKey.trim());

        SecretKey key = getSecretKey();
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(CLAIM_TYPE)
                .issuedAt(new Date(now))
                .expiration(new Date(now + VALIDITY_MS))
                .signWith(key)
                .compact();
    }

    public MypageLinkClaims parseMypageLinkToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("token is required");
        }
        try {
            SecretKey key = getSecretKey();
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token.trim())
                    .getPayload();

            Object type = claims.get("type");
            if (type == null || !CLAIM_TYPE.equals(String.valueOf(type))) {
                throw new IllegalArgumentException("Invalid token type");
            }

            MypageLinkClaims c = new MypageLinkClaims();
            c.setEsntlId(asString(claims.get("esntlId")));
            c.setUserSe(asString(claims.get("userSe")));
            c.setStateKey(asString(claims.get("stateKey")));
            return c;
        } catch (JwtException e) {
            log.warn("Invalid mypage link token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid mypage link token");
        }
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    public static class MypageLinkClaims {
        private String esntlId;
        private String userSe;
        private String stateKey;

        public String getEsntlId() {
            return esntlId;
        }

        public void setEsntlId(String esntlId) {
            this.esntlId = esntlId;
        }

        public String getUserSe() {
            return userSe;
        }

        public void setUserSe(String userSe) {
            this.userSe = userSe;
        }

        public String getStateKey() {
            return stateKey;
        }

        public void setStateKey(String stateKey) {
            this.stateKey = stateKey;
        }
    }
}
