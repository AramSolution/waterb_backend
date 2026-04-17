package egovframework.com.jwt;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.service.EgovProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

//security 관련 제외한 jwt util 클래스
@Slf4j
@Component
public class EgovJwtTokenUtil implements Serializable{

	private static final long serialVersionUID = -5180902194184255251L;

	// JWT 토큰 유효시간 (초 단위)
	public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60; // 24시간 (86400초)
	//public static final long JWT_TOKEN_VALIDITY = 60 * 60; // 1시간 (3600초)

	/** 아이디 찾기·비밀번호 찾기용 본인인증 recoveryToken(JWT) 유효시간 (초) */
	public static final long ACCOUNT_RECOVERY_TOKEN_VALIDITY_SECONDS = 15 * 60;

	public static final String CLAIM_PURPOSE = "purpose";
	public static final String PURPOSE_ACCOUNT_RECOVERY = "accountRecovery";
	public static final String CLAIM_DI = "di";

	// static final을 유지하되, Spring Context 초기화 후에 설정
	private static String SECRET_KEY_STRING;
	
	// Environment를 직접 주입받아 사용
	@Autowired
	private Environment environment;
	
	@PostConstruct
	private void init() {
		// Environment에서 직접 읽기 (가장 확실한 방법)
		String secretKey = environment.getProperty("Globals.jwt.secret");
		
		// Environment에서 못 찾으면 EgovProperties에서 읽기 (fallback)
		if (secretKey == null || secretKey.trim().isEmpty()) {
			secretKey = EgovProperties.getProperty("Globals.jwt.secret");
		}
		
		SECRET_KEY_STRING = secretKey;
		
		if (SECRET_KEY_STRING == null || SECRET_KEY_STRING.trim().isEmpty() || 
		    "99".equals(SECRET_KEY_STRING) || " EXCEPTION OCCURRED".equals(SECRET_KEY_STRING)) {
			log.error("JWT secret key is not configured! Please set Globals.jwt.secret in application-{profile}.properties");
			log.error("Current value: {}", SECRET_KEY_STRING);
		} else {
			log.info("JWT secret key loaded successfully. Length: {} characters", SECRET_KEY_STRING.length());
		}
	}

	/**
	 * SecretKey를 생성합니다. (io.jsonwebtoken 0.12.6 버전 호환)
	 * Base64로 인코딩된 키를 디코딩하여 사용합니다.
	 */
	private SecretKey getSecretKey() {
		if (SECRET_KEY_STRING == null || SECRET_KEY_STRING.trim().isEmpty() || 
		    "99".equals(SECRET_KEY_STRING) || " EXCEPTION OCCURRED".equals(SECRET_KEY_STRING)) {
			log.error("JWT secret key is not configured!");
			throw new IllegalArgumentException("JWT secret key is not configured");
		}
		
		try {
			// Base64로 디코딩
			byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY_STRING);
			
			// 키 길이 검증: 최소 256 bits (32 bytes) 필요
			if (keyBytes.length < 32) {
				log.error("JWT secret key is too short after Base64 decoding: {} bytes. Minimum 32 bytes (256 bits) required.", keyBytes.length);
				throw new IllegalArgumentException(
					String.format("JWT secret key must be at least 32 bytes (256 bits) after Base64 decoding, but was %d bytes", keyBytes.length)
				);
			}
			
			return Keys.hmacShaKeyFor(keyBytes);
		} catch (IllegalArgumentException e) {
			// Base64 디코딩 실패 또는 키 길이 부족
			log.error("Failed to create JWT secret key: {}", e.getMessage());
			log.error("Secret key string: {} (length: {})", SECRET_KEY_STRING, SECRET_KEY_STRING != null ? SECRET_KEY_STRING.length() : 0);
			throw new IllegalArgumentException("Invalid JWT secret key: " + e.getMessage(), e);
		}
	}

	// retrieve username from jwt token
	public String getUserIdFromToken(String token) {
		return getInfoFromToken("id", token);
	}

	public String getUserSeFromToken(String token) {
		return getInfoFromToken("userSe", token);
	}

	public String getInfoFromToken(String type, String token) {
		Claims claims = getClaimFromToken(token);
	    Object info = claims.get(type);

	    return info != null ? info.toString() : null;
	}

	public Claims getClaimFromToken(String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return claims;
	}

	//for retrieveing any information from token we will need the secret key
	public Claims getAllClaimsFromToken(String token) {
		log.debug("===>>> secret = "+SECRET_KEY_STRING);
		SecretKey key = getSecretKey();
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	//generate token for user
    public String generateToken(LoginVO loginVO) {
        return doGenerateToken(loginVO, "Authorization");
    }

	//while creating the token -
	//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	//2. Sign the JWT using the HS512 algorithm and secret key.
	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//   compaction of the JWT to a URL-safe string
	private String doGenerateToken(LoginVO loginVO, String subject) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", loginVO.getId() );
        claims.put("name", loginVO.getName() );
        claims.put("userSe", loginVO.getUserSe() );
        claims.put("orgnztId", loginVO.getOrgnztId() );
        claims.put("uniqId", loginVO.getUniqId() );
        claims.put("type", subject);
        claims.put("groupNm", loginVO.getGroupNm());//권한그룹으로 시프링시큐리티 사용

    	log.debug("===>>> secret = "+SECRET_KEY_STRING);
    	SecretKey key = getSecretKey();
        return Jwts.builder()
        		.claims(claims)
        		.subject(subject)
        		.issuedAt(new Date(System.currentTimeMillis()))
        		.expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
        		.signWith(key)
        		.compact();
    }

	public LoginVO getLoginVOFromToken(String token) throws InvalidJwtException{
		LoginVO loginVO = new LoginVO();

        try {
		    loginVO.setId(getUserIdFromToken(token));
			loginVO.setName(getInfoFromToken("name", token));
			loginVO.setUserSe(getUserSeFromToken(token));
			loginVO.setOrgnztId(getInfoFromToken("orgnztId", token));
			loginVO.setUniqId(getInfoFromToken("uniqId", token));
            loginVO.setGroupNm(getInfoFromToken("groupNm", token));

            if(loginVO.getId() == null) throw new InvalidJwtException("Missing id in token");
        } catch (IllegalArgumentException e) {
            throw new InvalidJwtException("Unable to verify JWT Token: " + e.getMessage());
        } catch (JwtException e) {
            throw new InvalidJwtException("Unable to verify JWT Token: " + e.getMessage());
        }

		return loginVO;
	}

	/**
	 * 본인인증(accountRecovery) 완료 후 클라이언트에 전달하는 단기 JWT.
	 * 로그인 JWT와 구분되며 {@link #parseAccountRecoveryToken(String)}으로만 해석한다.
	 */
	public String generateAccountRecoveryToken(String di, String userSe) {
		if (di == null || di.isBlank() || userSe == null || userSe.isBlank()) {
			throw new IllegalArgumentException("di and userSe are required for account recovery token");
		}
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_PURPOSE, PURPOSE_ACCOUNT_RECOVERY);
		claims.put(CLAIM_DI, di.trim());
		claims.put("userSe", userSe.trim());

		SecretKey key = getSecretKey();
		long validityMillis = ACCOUNT_RECOVERY_TOKEN_VALIDITY_SECONDS * 1000L;
		return Jwts.builder()
				.claims(claims)
				.subject("AccountRecovery")
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + validityMillis))
				.signWith(key)
				.compact();
	}

	public AccountRecoveryTokenClaims parseAccountRecoveryToken(String token) {
		if (token == null || token.isBlank()) {
			throw new InvalidJwtException("Empty recovery token");
		}
		try {
			Claims claims = getAllClaimsFromToken(token.trim());
			Object purpose = claims.get(CLAIM_PURPOSE);
			if (!PURPOSE_ACCOUNT_RECOVERY.equals(purpose != null ? purpose.toString() : null)) {
				throw new InvalidJwtException("Not an account recovery token");
			}
			Object di = claims.get(CLAIM_DI);
			Object userSe = claims.get("userSe");
			if (di == null || userSe == null) {
				throw new InvalidJwtException("Missing di or userSe in account recovery token");
			}
			return new AccountRecoveryTokenClaims(di.toString().trim(), userSe.toString().trim());
		} catch (JwtException e) {
			throw new InvalidJwtException("Invalid account recovery token: " + e.getMessage(), e);
		}
	}
}