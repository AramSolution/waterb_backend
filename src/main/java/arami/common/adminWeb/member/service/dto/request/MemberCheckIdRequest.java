package arami.common.adminWeb.member.service.dto.request;

import lombok.Data;

/**
 * 회원 ID 중복 체크 요청 DTO
 */
@Data
public class MemberCheckIdRequest {
    
    private String userId;       // 체크할 사용자 ID
    private String esntlId;      // 수정 시 제외할 고유 ID (선택적)
}
