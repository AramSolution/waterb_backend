package arami.userWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * 사용자웹 포털(/userWeb) 메인 카드 목록 요청 DTO.
 * - REQ_GB(학생/학부모/학원/멘토/학교) 필터 없이 조회한다.
 */
@Data
public class ArtpromUserMainCardListRequest {
    /** true면 홍보(PRO_TYPE=03)도 포함 */
    private Boolean includePromo;
}

