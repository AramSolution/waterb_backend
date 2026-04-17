package egovframework.com.ext.oauth.service;

public interface OAuthConfig {

	static final String GOOGLE_PROFILE_URL = "https://www.googleapis.com/plus/v1/people/me";
	static final String NAVER_PROFILE_URL = "https://openapi.naver.com/v1/nid/me";
	static final String KAKAO_PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

	static final String NAVER_ACCESS_TOKEN = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
	static final String NAVER_AUTH = "https://nid.naver.com/oauth2.0/authorize";

	/** ScribeJava가 POST 본문으로 client_id/secret 전달 — URL에 client_id 중복 시 KOE010 유발 가능 */
	static final String KAKAO_ACCESS_TOKEN = "https://kauth.kakao.com/oauth/token";
	static final String KAKAO_AUTH = "https://kauth.kakao.com/oauth/authorize";

	static final String APPLE_ACCESS_TOKEN = "https://appleid.apple.com/auth/token";

	static final String GOOGLE_SERVICE_NAME = "google";
	static final String NAVER_SERVICE_NAME = "naver";
	static final String KAKAO_SERVICE_NAME = "kakao";
	static final String APPLE_SERVICE_NAME = "apple";
}
