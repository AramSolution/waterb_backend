package arami.common.adminWeb.member.service.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 관리자 회원 상세 조회 응답 DTO
 */
@Data
public class MemberDetailResponse {
    
    private MemberDTO adminInfo;           // 회원 기본 정보
    private Object authInfo;                // 권한 정보
    private List<Object> degreeInfo;        // 학위 정보
    private List<Object> licenceInfo;       // 자격증 정보
    private List<Object> careerInfo;         // 경력 정보
}
