package arami.adminWeb.artappm.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 선정관리용 지원사업 신청 목록 조회 요청 DTO (PRO_ID만 조회, 페이징 없음)
 */
@Data
public class ArtappmSelectionListRequest {

    /** 지원사업코드 (PRO_ID) - 필수 */
    @NotBlank(message = "지원사업코드(proId)는 필수입니다.")
    private String proId;
}
