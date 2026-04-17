package arami.common.auth.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ARMUSER 본인인증 DI + 회원유형으로 로그인 아이디 조회용 파라미터.
 */
@Getter
@Setter
public class CrtfcDnUserSeParam {

    private String userSe;
    private String crtfcDnValue;
}
