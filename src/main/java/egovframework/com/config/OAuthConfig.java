package egovframework.com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import egovframework.com.ext.oauth.service.OAuthVO;

/**
 * OAuth 설정 클래스
 * 네이버, 카카오 OAuth 빈을 생성합니다.
 */
@Configuration
public class OAuthConfig {

	@Value("${OAuth.naver.ClientID}")
	private String naverClientId;

	@Value("${OAuth.naver.ClientSecret}")
	private String naverClientSecret;

	@Value("${OAuth.kakao.ClientID}")
	private String kakaoClientId;

	@Value("${OAuth.kakao.ClientSecret:}")
	private String kakaoClientSecret;

	@Value("${Globals.domain:http://localhost:8080}")
	private String domain;

	@Bean(name = "naverAuthVO")
	public OAuthVO naverAuthVO() {
		return new OAuthVO(
			egovframework.com.ext.oauth.service.OAuthConfig.NAVER_SERVICE_NAME,
			naverClientId,
			naverClientSecret,
			domain + "/OAuth/naver/callback"
		);
	}

	@Bean(name = "kakaoAuthVO")
	public OAuthVO kakaoAuthVO() {
		return new OAuthVO(
			egovframework.com.ext.oauth.service.OAuthConfig.KAKAO_SERVICE_NAME,
			kakaoClientId,
			kakaoClientSecret,
			domain + "/OAuth/kakao/callback"
		);
	}
}
