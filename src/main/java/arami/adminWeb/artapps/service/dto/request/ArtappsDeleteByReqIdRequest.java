package arami.adminWeb.artapps.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 지원사업신청ID(REQ_ID) 기준 공부의 명수 신청(ARTAPPM + ARTAPPS) 삭제 요청.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtappsDeleteByReqIdRequest {

    @NotBlank(message = "지원사업신청ID(reqId)는 필수입니다.")
    private String reqId;
}
