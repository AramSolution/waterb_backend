package arami.common.auth.service;

import egovframework.com.cmm.LoginVO;
import arami.common.auth.service.dto.CrtfcDnUserSeParam;
import arami.common.auth.service.dto.OAuthJoinCheckResult;
import arami.common.auth.service.dto.PasswordUpdateParam;
import arami.common.auth.service.dto.RecoveryMemberRow;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

@Repository("memberLoginDAO")
public class MemberLoginDAO extends EgovAbstractMapper {

    public LoginVO actionLogin(LoginVO loginVO) throws Exception{
        return (LoginVO) selectOne("memberLoginDAO.actionLogin", loginVO);
    }

    /**
     * OAuth 이메일 + 유형(USER_SE)으로 기존 회원 조회 (가입 여부 판단용).
     */
    public LoginVO actionLoginByOAuthEmail(String email, String userSe) throws Exception {
        if (email == null || email.isEmpty() || userSe == null || userSe.isEmpty()) {
            return null;
        }
        java.util.Map<String, String> param = new java.util.HashMap<>();
        param.put("email", email);
        param.put("userSe", userSe);
        return (LoginVO) selectOne("memberLoginDAO.actionLoginByOAuthEmail", param);
    }

    public OAuthJoinCheckResult selectOAuthJoinCheck(String email, String userSe) throws Exception {
        if (email == null || email.isEmpty() || userSe == null || userSe.isEmpty()) {
            return null;
        }
        java.util.Map<String, String> param = new java.util.HashMap<>();
        param.put("email", email);
        param.put("userSe", userSe);
        return (OAuthJoinCheckResult) selectOne("memberLoginDAO.selectOAuthJoinCheck", param);
    }

    /**
     * OAuth 정책상 USER_ID = 이메일 일 때, 해당 아이디로 가입된 활성 회원의 USER_SE (없으면 null).
     */
    public String selectUserSeByOauthUserId(String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        return (String) selectOne("memberLoginDAO.selectUserSeByOauthUserId", userId);
    }

    /**
     * 본인인증 DI(CRTFC_DN_VALUE) + 회원유형(USER_SE)으로 활성 회원의 로그인 아이디(USER_ID) 조회.
     */
    public String selectUserIdByCrtfcDnAndUserSe(CrtfcDnUserSeParam param) throws Exception {
        if (param == null
                || param.getCrtfcDnValue() == null
                || param.getCrtfcDnValue().isEmpty()
                || param.getUserSe() == null
                || param.getUserSe().isEmpty()) {
            return null;
        }
        return (String) selectOne("memberLoginDAO.selectUserIdByCrtfcDnAndUserSe", param);
    }

    /**
     * 본인인증 DI + USER_SE로 비탈퇴 회원 ESNTL_ID·USER_ID 조회 (비밀번호 재설정).
     */
    public RecoveryMemberRow selectRecoveryMemberByCrtfcDnAndUserSe(CrtfcDnUserSeParam param) throws Exception {
        if (param == null
                || param.getCrtfcDnValue() == null
                || param.getCrtfcDnValue().isEmpty()
                || param.getUserSe() == null
                || param.getUserSe().isEmpty()) {
            return null;
        }
        return (RecoveryMemberRow) selectOne("memberLoginDAO.selectRecoveryMemberByCrtfcDnAndUserSe", param);
    }

    public int updatePasswordByEsntlId(PasswordUpdateParam param) throws Exception {
        if (param == null
                || param.getEsntlId() == null
                || param.getEsntlId().isEmpty()
                || param.getEncryptedPassword() == null
                || param.getEncryptedPassword().isEmpty()) {
            return 0;
        }
        return update("memberLoginDAO.updatePasswordByEsntlId", param);
    }

    public int updateOAuthId(String esntlId, String oauthGb, String naverAuthId, String kakaoAuthId) throws Exception {
        if (esntlId == null || esntlId.isEmpty()) {
            return 0;
        }
        java.util.Map<String, String> param = new java.util.HashMap<>();
        param.put("esntlId", esntlId);
        param.put("oauthGb", oauthGb);
        param.put("naverAuthId", naverAuthId);
        param.put("kakaoAuthId", kakaoAuthId);
        return update("memberLoginDAO.updateOAuthId", param);
    }

    public int unlinkOAuthId(String esntlId, String oauthService) throws Exception {
        if (esntlId == null || esntlId.isEmpty() || oauthService == null || oauthService.isEmpty()) {
            return 0;
        }
        java.util.Map<String, String> param = new java.util.HashMap<>();
        param.put("esntlId", esntlId);
        param.put("oauthService", oauthService);
        return update("memberLoginDAO.unlinkOAuthId", param);
    }

    public LoginVO actionLoginByEsntlId(String esntlId) throws Exception {
        if (esntlId == null || esntlId.isEmpty()) {
            return null;
        }
        return (LoginVO) selectOne("memberLoginDAO.actionLoginByEsntlId", esntlId);
    }

    /** 다른 회원이 이미 사용 중인 네이버 OAuth id 인지 (본인 ESNTL_ID 제외) */
    public String selectEsntlIdByNaverAuthIdExcluding(String naverAuthId, String excludeEsntlId) throws Exception {
        if (naverAuthId == null || naverAuthId.isEmpty()) {
            return null;
        }
        java.util.Map<String, String> param = new java.util.HashMap<>();
        param.put("naverAuthId", naverAuthId);
        param.put("excludeEsntlId", excludeEsntlId != null ? excludeEsntlId : "");
        return (String) selectOne("memberLoginDAO.selectEsntlIdByNaverAuthIdExcluding", param);
    }

    /** 다른 회원이 이미 사용 중인 카카오 OAuth id 인지 (본인 ESNTL_ID 제외) */
    public String selectEsntlIdByKakaoAuthIdExcluding(String kakaoAuthId, String excludeEsntlId) throws Exception {
        if (kakaoAuthId == null || kakaoAuthId.isEmpty()) {
            return null;
        }
        java.util.Map<String, String> param = new java.util.HashMap<>();
        param.put("kakaoAuthId", kakaoAuthId);
        param.put("excludeEsntlId", excludeEsntlId != null ? excludeEsntlId : "");
        return (String) selectOne("memberLoginDAO.selectEsntlIdByKakaoAuthIdExcluding", param);
    }

}
