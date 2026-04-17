package arami.common.adminWeb.member.service.dto.request;

import lombok.Data;

/**
 * 관리자 회원 상세 조회 요청 DTO
 */
@Data
public class MemberDetailRequest {
    
    private String esntlId;      // 고유 ID
}
