package arami.userWeb.oauth.service;

import arami.userWeb.oauth.service.dto.request.OAuthLinkConfirmRequest;
import arami.userWeb.oauth.service.dto.request.OAuthLinkUnlinkRequest;
import arami.userWeb.oauth.service.dto.response.OAuthLinkConfirmResponse;

public interface OAuthLinkService {
    OAuthLinkConfirmResponse confirmLink(OAuthLinkConfirmRequest request);

    /**
     * OAuth 연동 해지 (unlink)
     * - 현재 로그인 사용자 기준으로 NAVER_AUTH_ID / KAKAO_AUTH_ID 제거
     */
    void unlinkLink(OAuthLinkUnlinkRequest request);
}

