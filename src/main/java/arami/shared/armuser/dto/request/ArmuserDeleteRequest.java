package arami.shared.armuser.dto.request;

import lombok.Data;

/**
 * ARMUSER 탈퇴(삭제) 요청 DTO (MBER_STTUS='D' 처리)
 */
@Data
public class ArmuserDeleteRequest {

    private String esntlId;
}
