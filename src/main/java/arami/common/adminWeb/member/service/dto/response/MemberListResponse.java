package arami.common.adminWeb.member.service.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 관리자 회원 리스트 응답 DTO
 */
@Data
public class MemberListResponse {
    
    private List<MemberDTO> data;           // 회원 리스트
    private Integer recordsTotal;          // 전체 레코드 수
    private Integer recordsFiltered;        // 필터링된 레코드 수
    private String result;                 // 결과 코드 ("00": 성공, "01": 실패)
}
