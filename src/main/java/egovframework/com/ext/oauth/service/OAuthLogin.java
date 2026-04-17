package egovframework.com.ext.oauth.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.OAuthResponseException;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import egovframework.com.cmm.service.EgovProperties;


public class OAuthLogin {

	private static final Logger log = LoggerFactory.getLogger(OAuthLogin.class);

	private OAuth20Service oauthService;
	private OAuthVO oauthVO;
	/** OAuth URL에 넣을 state (student, parent, academy, mentor 등). ServiceBuilder에 state()가 없어 URL 후처리로 붙임. */
	private String customState;

	public OAuthLogin(OAuthVO oauthVO) {
		this(oauthVO, null);
	}

	/**
	 * state 파라미터를 포함한 OAuth URL 생성을 위해 사용 (예: student, parent, academy, mentor).
	 * ScribeJava 8.3.1 ServiceBuilder에는 state() 메서드가 없어, URL 발급 후 쿼리에 state를 붙임.
	 */
	public OAuthLogin(OAuthVO oauthVO, String state) {
		ServiceBuilder builder = new ServiceBuilder(oauthVO.getClientId())
				.callback(oauthVO.getRedirectUrl());
		// Kakao client secret disabled mode: do not send client_secret parameter at all.
		if (!(oauthVO.isKakao() && StringUtils.isBlank(oauthVO.getClientSecret()))) {
			builder = builder.apiSecret(oauthVO.getClientSecret());
		}
		this.oauthService = builder.build(oauthVO.getApi20Instance());
		this.oauthVO = oauthVO;
		this.customState = (state != null && !state.isEmpty()) ? state : null;
	}

	public String getOAuthURL() {
		String url = this.oauthService.getAuthorizationUrl();
		if (customState != null) {
			String enc = URLEncoder.encode(customState, StandardCharsets.UTF_8);
			if (url.contains("state=")) {
				url = url.replaceAll("state=[^&]*", "state=" + enc);
			} else {
				url = url + (url.contains("?") ? "&" : "?") + "state=" + enc;
			}
		}
		return url;
	}

	public OAuthUniversalUser getUserProfile(String code) throws Exception {
		final OAuth2AccessToken accessToken;
		try {
			accessToken = oauthService.getAccessToken(code);
		} catch (OAuthResponseException e) {
			logOAuthTokenEndpointFailure(e);
			throw e;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("[OAuth token] access token request interrupted: service={}", oauthVO.getService(), e);
			throw e;
		} catch (IOException | ExecutionException e) {
			log.error("[OAuth token] access token request failed: service={} type={} message={}",
					oauthVO.getService(), e.getClass().getSimpleName(), e.getMessage(), e);
			throw e;
		}

		OAuthRequest request = new OAuthRequest(Verb.GET, this.oauthVO.getProfileUrl());
		oauthService.signRequest(accessToken, request);

		Response response = oauthService.execute(request);

		return parseJson(response.getBody());
	}

	/** 토큰 엔드포인트(oauth/token) 오류 시 HTTP 상태·본문·OAuth2 코드 로깅 (client_secret 미기록). */
	private void logOAuthTokenEndpointFailure(OAuthResponseException e) {
		final Response r = e.getResponse();
		final int httpStatus = r != null ? r.getCode() : -1;
		String responseBody = "";
		if (r != null) {
			try {
				responseBody = r.getBody();
			} catch (IOException ioe) {
				responseBody = "(could not read body: " + ioe.getMessage() + ")";
			}
		}
		if (e instanceof OAuth2AccessTokenErrorResponse) {
			final OAuth2AccessTokenErrorResponse te = (OAuth2AccessTokenErrorResponse) e;
			log.error(
					"[OAuth token] token endpoint error: service={} redirectUri={} httpStatus={} oauth2Error={} errorDescription={} responseBody={}",
					oauthVO.getService(),
					oauthVO.getRedirectUrl(),
					httpStatus,
					te.getError(),
					te.getErrorDescription(),
					responseBody);
		} else {
			log.error(
					"[OAuth token] token endpoint error: service={} redirectUri={} httpStatus={} responseBody={}",
					oauthVO.getService(),
					oauthVO.getRedirectUrl(),
					httpStatus,
					responseBody);
		}
	}

	private OAuthUniversalUser parseJson(String body) throws Exception {
		OAuthUniversalUser user = new OAuthUniversalUser();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(body);

		if (this.oauthVO.isGoogle()) {
		    try {
		        String id = rootNode.get("id").asText();
		        user.setServiceName(OAuthConfig.GOOGLE_SERVICE_NAME);
		        if (oauthVO.isGoogle())
		            user.setUserId(id);
		        user.setNickName(rootNode.get("displayName").asText());
		        JsonNode nameNode = rootNode.path("name");
		        String uname = nameNode.get("familyName").asText() + nameNode.get("givenName").asText();
		        user.setUserName(uname);

		        Iterator<JsonNode> iterEmails = rootNode.path("emails").elements();
		        while(iterEmails.hasNext()) {
		            JsonNode emailNode = iterEmails.next();
		            String type = emailNode.get("type").asText();
		            if (StringUtils.equals(type, "account")) {
		                user.setEmail(emailNode.get("value").asText());
		                break;
		            }
		        }
		    }catch(Exception e) {
		        if(EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
		            e.printStackTrace();
		            System.out.println("Connection Exception occurred");
		        }
		    }

		} else if (this.oauthVO.isNaver()) {
			user.setServiceName(OAuthConfig.NAVER_SERVICE_NAME);
			JsonNode resNode = rootNode.get("response");
			user.setUserId(resNode.get("id").asText());
			user.setUserName(resNode.get("name").asText());
			
			if(resNode.get("mobile") != null && !resNode.get("mobile").isNull()) {
				user.setPhoneNumber(resNode.get("mobile").asText().replace("-", ""));
			} else {
				user.setPhoneNumber("");
			}

			if(resNode.get("email") != null && !resNode.get("email").isNull()) {
			    user.setEmail(resNode.get("email").asText());
			}else {
			    user.setEmail("");
			}

			if(resNode.get("profile_image") != null && !resNode.get("profile_image").isNull()) {
			    user.setProfileImage(resNode.get("profile_image").asText());
			}else {
			    user.setProfileImage("");
			}
			
			// 네이버 프로필 응답(이메일/전화/프로필이미지 등) 디버깅 로그
			// Globals.debug=true 환경에서만 원문/파싱 결과를 남깁니다.
			if ("true".equalsIgnoreCase(EgovProperties.getProperty("Globals.debug"))) {
				log.info("[OAuthLogin/Naver] response.raw={}", resNode);
				log.info(
						"[OAuthLogin/Naver] parsed userId={} userName={} email={} phoneNumber={} profileImage={}",
						user.getUserId(),
						user.getUserName(),
						user.getEmail(),
						user.getPhoneNumber(),
						user.getProfileImage());
			}

		} else if (this.oauthVO.isKakao()) {
			user.setServiceName(OAuthConfig.KAKAO_SERVICE_NAME);
			user.setUserId(rootNode.get("id").asText());

			JsonNode resNode = rootNode.get("kakao_account");
			if(resNode != null && resNode.get("name") != null && !resNode.get("name").isNull()) {
				user.setUserName(resNode.get("name").asText());
			}else{
				user.setUserName("");
			}

			if(resNode != null && resNode.get("email") != null && !resNode.get("email").isNull()) {
				user.setEmail(resNode.get("email").asText());
			}else{
				user.setEmail("");
			}

			if(resNode != null && resNode.get("phone_number") != null && !resNode.get("phone_number").isNull()) {
				user.setPhoneNumber(resNode.get("phone_number").asText());
			}else{
				user.setPhoneNumber("");
			}

			if(resNode != null && resNode.get("ci") != null && !resNode.get("ci").isNull()) {
				user.setCertCi(resNode.get("ci").asText());
			}else{
				user.setCertCi("");
			}

			if(rootNode.get("properties") != null) {
				resNode = rootNode.get("properties");
				if(resNode.get("nickname") != null && !resNode.get("nickname").isNull()) {
					user.setNickName(resNode.get("nickname").asText());
				}else{
					user.setNickName("");
				}
				if(resNode.get("profile_image") != null && !resNode.get("profile_image").isNull()) {
					user.setProfileImage(resNode.get("profile_image").asText());
				}else {
					user.setProfileImage("");
				}
			}else{
				user.setNickName("");
				user.setProfileImage("");
			}
		}

		return user;
	}

}
