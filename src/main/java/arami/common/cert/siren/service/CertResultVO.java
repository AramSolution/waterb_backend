package arami.common.cert.siren.service;

import lombok.Data;

@Data
public class CertResultVO {

    private String celNo;
    private String userName;
    private String birYMD;
    /** 개인식별코드(DI) - 보호자 본인일치 여부 검증용 */
    private String di;
    private String rspCd;
    private String resMsg;
    /** certFlow=accountRecovery — 아이디/비밀번호 찾기용 단기 JWT(프론트 → /auth/account-recovery/find-user-id) */
    private String recoveryToken;

}
