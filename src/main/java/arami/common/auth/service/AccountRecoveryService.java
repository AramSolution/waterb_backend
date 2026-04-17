package arami.common.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arami.common.auth.service.dto.CrtfcDnUserSeParam;
import arami.common.auth.service.dto.FindUserIdByCrtfcDnRequest;
import arami.common.auth.service.dto.FindUserIdByRecoveryTokenRequest;
import arami.common.auth.service.dto.PasswordResetRequest;
import arami.common.auth.service.dto.PasswordUpdateParam;
import arami.common.auth.service.dto.RecoveryMemberRow;
import egovframework.com.jwt.AccountRecoveryTokenClaims;
import egovframework.com.jwt.EgovJwtTokenUtil;
import egovframework.com.jwt.InvalidJwtException;
import egovframework.let.utl.sim.service.EgovFileScrty;
import lombok.RequiredArgsConstructor;

/**
 * 본인인증 기반 아이디 찾기(/auth/find-user-id, /auth/account-recovery/find-user-id).
 * DB 조건: 탈퇴(MBER_STTUS='D')만 제외 — 승인대기(A)·정상(P) 등 비탈퇴 회원 포함.
 */
@Service
@RequiredArgsConstructor
public class AccountRecoveryService {

    private static final int MIN_NEW_PASSWORD_LENGTH = 8;

    private final MemberLoginDAO memberLoginDAO;
    private final EgovJwtTokenUtil jwtTokenUtil;

    /**
     * 본인인증 기반 비밀번호 재설정. recoveryToken 또는 crtfcDnValue로 회원 특정 후 PASSWORD 갱신.
     */
    @Transactional
    public Map<String, Object> requestPasswordReset(PasswordResetRequest request) throws Exception {
        Map<String, Object> body = new HashMap<>();
        String userSe = request.getUserSe() != null ? request.getUserSe().trim() : "";
        String newPw = request.getNewPassword() != null ? request.getNewPassword().trim() : "";
        if (userSe.isEmpty() || newPw.isEmpty()) {
            body.put("resultCode", "400");
            body.put("resultMessage", "회원 유형 또는 새 비밀번호가 없습니다.");
            return body;
        }
        if (newPw.length() < MIN_NEW_PASSWORD_LENGTH) {
            body.put("resultCode", "400");
            body.put("resultMessage", "비밀번호는 " + MIN_NEW_PASSWORD_LENGTH + "자 이상 입력해 주세요.");
            return body;
        }

        String recovery = request.getRecoveryToken() != null ? request.getRecoveryToken().trim() : "";
        String dn = request.getCrtfcDnValue() != null ? request.getCrtfcDnValue().trim() : "";

        RecoveryMemberRow row;
        if (!recovery.isEmpty()) {
            AccountRecoveryTokenClaims claims;
            try {
                claims = jwtTokenUtil.parseAccountRecoveryToken(recovery);
            } catch (InvalidJwtException e) {
                body.put("resultCode", "400");
                body.put("resultMessage", "유효하지 않거나 만료된 인증입니다. 본인인증을 다시 진행해 주세요.");
                return body;
            }
            if (!userSe.equals(claims.getUserSe())) {
                body.put("resultCode", "400");
                body.put("resultMessage", "회원 유형이 일치하지 않습니다.");
                return body;
            }
            row = selectRecoveryMemberRow(claims.getUserSe(), claims.getDi());
        } else if (!dn.isEmpty()) {
            row = selectRecoveryMemberRow(userSe, dn);
            String expectUserId = request.getUserId() != null ? request.getUserId().trim() : "";
            if (!expectUserId.isEmpty() && row != null
                    && row.getUserId() != null
                    && !expectUserId.equals(row.getUserId().trim())) {
                body.put("resultCode", "400");
                body.put("resultMessage", "아이디 정보가 일치하지 않습니다.");
                return body;
            }
        } else {
            body.put("resultCode", "400");
            body.put("resultMessage", "본인인증 정보(recoveryToken 또는 crtfcDnValue)가 필요합니다.");
            return body;
        }

        if (row == null || row.getEsntlId() == null || row.getEsntlId().isBlank()
                || row.getUserId() == null || row.getUserId().isBlank()) {
            body.put("resultCode", "300");
            body.put("resultMessage", "일치하는 회원 정보가 없습니다.");
            return body;
        }

        String enc;
        try {
            enc = EgovFileScrty.encryptPassword(newPw, row.getUserId().trim());
        } catch (Exception e) {
            body.put("resultCode", "500");
            body.put("resultMessage", "비밀번호 처리 중 오류가 발생했습니다.");
            return body;
        }

        int updated = memberLoginDAO.updatePasswordByEsntlId(
                new PasswordUpdateParam(row.getEsntlId().trim(), enc));
        if (updated < 1) {
            body.put("resultCode", "300");
            body.put("resultMessage", "비밀번호를 변경하지 못했습니다. 다시 시도해 주세요.");
            return body;
        }

        body.put("resultCode", "200");
        body.put("resultMessage", "비밀번호가 변경되었습니다.");
        return body;
    }

    private RecoveryMemberRow selectRecoveryMemberRow(String userSe, String crtfcDnValue) throws Exception {
        CrtfcDnUserSeParam p = new CrtfcDnUserSeParam();
        p.setUserSe(userSe);
        p.setCrtfcDnValue(crtfcDnValue);
        return memberLoginDAO.selectRecoveryMemberByCrtfcDnAndUserSe(p);
    }

    public Map<String, Object> findUserIdByCrtfcDn(FindUserIdByCrtfcDnRequest request) throws Exception {
        Map<String, Object> body = new HashMap<>();
        String userSe = request.getUserSe() != null ? request.getUserSe().trim() : "";
        String dn = request.getCrtfcDnValue() != null ? request.getCrtfcDnValue().trim() : "";
        if (userSe.isEmpty() || dn.isEmpty()) {
            body.put("resultCode", "400");
            body.put("resultMessage", "회원 유형 또는 본인인증 정보가 없습니다.");
            return body;
        }
        String userId = lookupUserId(userSe, dn);
        if (userId == null || userId.isBlank()) {
            body.put("resultCode", "300");
            body.put("resultMessage", "일치하는 회원 정보가 없습니다.");
            return body;
        }
        body.put("resultCode", "200");
        body.put("resultMessage", "성공");
        body.put("userId", userId.trim());
        body.put("maskedUserId", null);
        return body;
    }

    public Map<String, Object> findUserIdByRecoveryToken(FindUserIdByRecoveryTokenRequest request) throws Exception {
        Map<String, Object> body = new HashMap<>();
        String raw = request.getRecoveryToken() != null ? request.getRecoveryToken().trim() : "";
        if (raw.isEmpty()) {
            body.put("resultCode", "400");
            body.put("resultMessage", "인증 토큰이 없습니다.");
            return body;
        }
        AccountRecoveryTokenClaims claims;
        try {
            claims = jwtTokenUtil.parseAccountRecoveryToken(raw);
        } catch (InvalidJwtException e) {
            body.put("resultCode", "400");
            body.put("resultMessage", "유효하지 않거나 만료된 인증입니다. 본인인증을 다시 진행해 주세요.");
            return body;
        }
        String userId = lookupUserId(claims.getUserSe(), claims.getDi());
        if (userId == null || userId.isBlank()) {
            body.put("resultCode", "300");
            body.put("resultMessage", "일치하는 회원 정보가 없습니다.");
            return body;
        }
        String trimmed = userId.trim();
        body.put("resultCode", "200");
        body.put("resultMessage", "성공");
        body.put("userId", null);
        body.put("maskedUserId", maskLoginId(trimmed));
        return body;
    }

    private String lookupUserId(String userSe, String crtfcDnValue) throws Exception {
        CrtfcDnUserSeParam p = new CrtfcDnUserSeParam();
        p.setUserSe(userSe);
        p.setCrtfcDnValue(crtfcDnValue);
        return memberLoginDAO.selectUserIdByCrtfcDnAndUserSe(p);
    }

    private static String maskLoginId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return "";
        }
        if (userId.length() <= 3) {
            return "***";
        }
        return userId.substring(0, 2) + "***" + userId.substring(userId.length() - 1);
    }
}
