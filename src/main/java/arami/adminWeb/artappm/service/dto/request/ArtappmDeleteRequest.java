package arami.adminWeb.artappm.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 지원사업 신청 삭제 요청 DTO (REQ_ID 기준)
 */
@Data
public class ArtappmDeleteRequest {

    /** 지원사업신청 PK (REQ_ID). 수정/삭제는 REQ_ID만 사용 */
    @NotBlank(message = "지원사업신청ID(reqId)는 필수입니다.")
    private String reqId;
}
