package arami.common.auth.service.dto;

import lombok.Getter;
import lombok.Setter;

/** ARMUSER 본인인증(DI+USER_SE)으로 조회한 ESNTL_ID / USER_ID */
@Getter
@Setter
public class RecoveryMemberRow {

    private String esntlId;
    private String userId;
}
