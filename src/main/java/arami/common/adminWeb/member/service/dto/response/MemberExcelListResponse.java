package arami.common.adminWeb.member.service.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 관리자 회원 엑셀 리스트 응답 DTO
 * 
 * Note: Service에서 아직 List<Object>를 반환하므로, 
 * 나중에 Service를 DTO로 변경할 때 List<MemberDTO>로 변경 예정
 */
@Data
public class MemberExcelListResponse {
    
    private List<Object> data;       // 회원 리스트 (나중에 List<MemberDTO>로 변경 예정)
    private String result;            // 결과 코드 ("00": 성공, "01": 실패)
}
