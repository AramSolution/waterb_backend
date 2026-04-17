package arami.common.adminWeb.member.service.dto.request;

import lombok.Data;

/**
 * 관리자 회원 삭제 요청 DTO
 */
@Data
public class MemberDeleteRequest {
    
    private String esntlId;      // 고유 ID
}
