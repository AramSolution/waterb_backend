package arami.common.auth.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * POST /auth/account-recovery/find-user-id — 본인인증 후 발급된 recoveryToken(JWT)으로 아이디 조회.
 */
@Getter
@Setter
public class FindUserIdByRecoveryTokenRequest {

    @NotBlank
    private String recoveryToken;
}
