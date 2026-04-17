package arami.common.adminWeb.member.service.dto.response;

import lombok.Data;

/**
 * 관리자회원 API 공통 결과 응답 DTO
 */
@Data
public class AdminMemberResultResponse {

    private String result;      // 결과 코드 ("00": 성공, "01": 실패, "50": 중복 등)
    private String message;     // 결과 메시지
}
