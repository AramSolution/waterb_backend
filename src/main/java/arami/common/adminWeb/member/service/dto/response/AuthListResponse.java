package arami.common.adminWeb.member.service.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 권한 리스트 응답 DTO
 */
@Data
public class AuthListResponse {
    
    private List<Object> authList;   // 권한 리스트 (나중에 AuthDTO로 변경 가능)
}
