package arami.common.auth.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * POST /auth/password-reset/request — 본인인증 기반 비밀번호 재설정.
 * {@code recoveryToken} 또는 {@code crtfcDnValue} 중 하나는 필수.
 */
@Getter
@Setter
public class PasswordResetRequest {

    @NotBlank
    private String userSe;

    /** 평문 비밀번호 — 서버에서 EgovFileScrty.encryptPassword(plain, userId) 후 저장 */
    @NotBlank
    private String newPassword;

    /** 본인인증 accountRecovery JWT */
    private String recoveryToken;

    /** 본인인증 DI( recoveryToken 미사용 시 ) */
    private String crtfcDnValue;

    /** 선택: 클라이언트가 조회한 로그인 아이디와 DB 일치 검증용 */
    private String userId;
}
