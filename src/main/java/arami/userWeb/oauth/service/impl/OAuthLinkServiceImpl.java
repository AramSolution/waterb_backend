package arami.userWeb.oauth.service.impl;

import arami.common.auth.service.MemberLoginService;
import arami.common.auth.service.dto.OAuthJoinCheckResult;
import arami.common.error.BusinessException;
import arami.common.error.ErrorCode;
import arami.userWeb.oauth.service.OAuthLinkService;
import arami.userWeb.oauth.service.dto.request.OAuthLinkConfirmRequest;
import arami.userWeb.oauth.service.dto.request.OAuthLinkUnlinkRequest;
import arami.userWeb.oauth.service.dto.response.OAuthLinkConfirmResponse;
import egovframework.com.cmm.LoginVO;
import egovframework.com.jwt.EgovJwtTokenUtil;
import egovframework.com.jwt.OAuthLinkTokenClaims;
import egovframework.com.jwt.OAuthLinkTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OAuthLinkServiceImpl implements OAuthLinkService {

    private final OAuthLinkTokenUtil oAuthLinkTokenUtil;
    private final MemberLoginService memberLoginService;
    private final EgovJwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public OAuthLinkConfirmResponse confirmLink(OAuthLinkConfirmRequest request) {
        OAuthLinkTokenClaims claims = oAuthLinkTokenUtil.parseLinkToken(request.getLinkToken());
        String email = claims.getEmail() != null ? claims.getEmail().trim() : "";
        String userSe = claims.getUserSe();
        String oauthService = claims.getOauthService();
        String oauthGb = claims.getOauthGb();
        String oauthAuthId = claims.getOauthAuthId();

        if (!StringUtils.hasText(userSe) || !StringUtils.hasText(oauthService) || !StringUtils.hasText(oauthAuthId)) {
            throw new IllegalArgumentException("Invalid link token");
        }

        // MY PAGE 연동: 현재 로그인한 사용자 기준으로 연동 (로그인 연동 플로우에서는 principal이 없으므로 email로 fallback)
        String targetEsntlId = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        if (principal instanceof LoginVO) {
            LoginVO loginVO = (LoginVO) principal;
            if (StringUtils.hasText(loginVO.getUniqId())) {
                targetEsntlId = loginVO.getUniqId();
            }
        }
        if (!StringUtils.hasText(targetEsntlId)) {
            if (!StringUtils.hasText(email)) {
                throw new IllegalArgumentException("Invalid link token");
            }
            OAuthJoinCheckResult join = safeJoinCheck(email, userSe);
            if (join == null || !StringUtils.hasText(join.getEsntlId())) {
                throw new IllegalArgumentException("User not found");
            }
            targetEsntlId = join.getEsntlId();
        }

        String naverAuthId = null;
        String kakaoAuthId = null;
        if ("naver".equalsIgnoreCase(oauthService)) {
            naverAuthId = oauthAuthId;
        } else if ("kakao".equalsIgnoreCase(oauthService)) {
            kakaoAuthId = oauthAuthId;
        } else {
            throw new IllegalArgumentException("Unsupported oauth service");
        }

        try {
            // 다른 계정에 이미 연동된 OAuth ID면 연동 불가
            if (naverAuthId != null) {
                String other = memberLoginService.selectEsntlIdByNaverAuthIdExcluding(naverAuthId, targetEsntlId);
                if (StringUtils.hasText(other)) {
                    throw new BusinessException(ErrorCode.OAUTH_ALREADY_LINKED);
                }
            }
            if (kakaoAuthId != null) {
                String other = memberLoginService.selectEsntlIdByKakaoAuthIdExcluding(kakaoAuthId, targetEsntlId);
                if (StringUtils.hasText(other)) {
                    throw new BusinessException(ErrorCode.OAUTH_ALREADY_LINKED);
                }
            }

            memberLoginService.updateOAuthId(targetEsntlId, oauthGb, naverAuthId, kakaoAuthId);
            LoginVO loginVO = memberLoginService.actionLoginByEsntlId(targetEsntlId);
            if (loginVO == null || !StringUtils.hasText(loginVO.getId())) {
                throw new IllegalArgumentException("Login failed");
            }
            loginVO.setPassword("secret!!!");
            String jwt = jwtTokenUtil.generateToken(loginVO);
            return new OAuthLinkConfirmResponse(jwt, claims.getState(), loginVO.getUserSe(), loginVO.getUniqId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Link confirm failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void unlinkLink(OAuthLinkUnlinkRequest request) {
        if (request == null || !StringUtils.hasText(request.getOauthService())) {
            throw new IllegalArgumentException("Invalid request");
        }

        String oauthService = request.getOauthService().trim();
        if (!"naver".equalsIgnoreCase(oauthService) && !"kakao".equalsIgnoreCase(oauthService)) {
            throw new IllegalArgumentException("Unsupported oauth service");
        }
        oauthService = oauthService.toLowerCase();

        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        if (!(principal instanceof LoginVO)) {
            throw new IllegalArgumentException("Not authenticated");
        }

        LoginVO loginVO = (LoginVO) principal;
        String esntlId = loginVO.getUniqId();
        if (!StringUtils.hasText(esntlId)) {
            throw new IllegalArgumentException("User id is missing in token");
        }

        try {
            memberLoginService.unlinkOAuthId(esntlId, oauthService);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unlink failed: " + e.getMessage(), e);
        }
    }

    private OAuthJoinCheckResult safeJoinCheck(String email, String userSe) {
        try {
            return memberLoginService.selectOAuthJoinCheck(email, userSe);
        } catch (Exception e) {
            throw new IllegalArgumentException("Join check failed: " + e.getMessage(), e);
        }
    }
}

