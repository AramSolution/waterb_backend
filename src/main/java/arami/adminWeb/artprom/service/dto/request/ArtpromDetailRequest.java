package arami.adminWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * 지원사업 상세 조회 / 삭제 요청 DTO
 * 상세: #{proId}, 삭제: #{proId}, #{UNIQ_ID}(CHG_USER_ID)
 */
@Data
public class ArtpromDetailRequest {

    private String proId;
    /** 삭제 시 변경자 ID (CHG_USER_ID) */
    private String UNIQ_ID;
}
