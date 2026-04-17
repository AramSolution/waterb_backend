package arami.common.auth.service;

import egovframework.com.cmm.LoginVO;
import arami.common.auth.service.dto.OAuthJoinCheckResult;

public interface MemberLoginService {

    /**
     * 일반 로그인 처리
     * @param loginVO
     * @return
     * @throws Exception
     */
    public LoginVO actionLogin(LoginVO loginVO) throws Exception;

    /**
     * OAuth 이메일 + 유형으로 기존 회원 조회 (가입 여부 판단용).
     * @param email OAuth에서 받은 이메일
     * @param userSe USER_SE (SNR, PNR, ANR, MNR)
     * @return 기존 회원이면 LoginVO, 없으면 null
     */
    public LoginVO actionLoginByOAuthEmail(String email, String userSe) throws Exception;

    public OAuthJoinCheckResult selectOAuthJoinCheck(String email, String userSe) throws Exception;

    /**
     * OAuth 즉시가입용: USER_ID(이메일)에 해당하는 활성 회원의 USER_SE. 없으면 null.
     */
    public String selectUserSeByOauthUserId(String userId) throws Exception;

    public int updateOAuthId(String esntlId, String oauthGb, String naverAuthId, String kakaoAuthId) throws Exception;

    /**
     * OAuth 연동 해지 (unlink)
     * - oauthService=nave/kakao 에 해당하는 *_AUTH_ID를 제거하고 OAUTH_GB를 남은 연동 상태로 재계산
     */
    public int unlinkOAuthId(String esntlId, String oauthService) throws Exception;

    public LoginVO actionLoginByEsntlId(String esntlId) throws Exception;

    /** 다른 ESNTL_ID가 해당 네이버 OAuth id를 쓰고 있으면 그 ESNTL_ID, 없으면 null */
    public String selectEsntlIdByNaverAuthIdExcluding(String naverAuthId, String excludeEsntlId) throws Exception;

    /** 다른 ESNTL_ID가 해당 카카오 OAuth id를 쓰고 있으면 그 ESNTL_ID, 없으면 null */
    public String selectEsntlIdByKakaoAuthIdExcluding(String kakaoAuthId, String excludeEsntlId) throws Exception;

}