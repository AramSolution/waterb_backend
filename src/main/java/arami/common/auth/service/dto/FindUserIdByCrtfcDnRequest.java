package arami.common.auth.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * POST /auth/find-user-id — 본인인증 DI(crtfcDnValue) + 회원유형(userSe)로 아이디 조회.
 */
@Getter
@Setter
public class FindUserIdByCrtfcDnRequest {

    @NotBlank
    private String userSe;

    @NotBlank
    private String crtfcDnValue;
}
