package egovframework.com.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 본인인증 accountRecovery JWT에서 추출한 DI + 회원유형. */
@Getter
@AllArgsConstructor
public class AccountRecoveryTokenClaims {

    private final String di;
    private final String userSe;
}
