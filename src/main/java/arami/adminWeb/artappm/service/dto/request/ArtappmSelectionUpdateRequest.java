package arami.adminWeb.artappm.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 선정관리 선정여부 1건 변경 요청 DTO (Y:선정, N:미선정, R:예비). REQ_ID 기준.
 */
@Data
public class ArtappmSelectionUpdateRequest {

    @NotBlank(message = "지원사업신청ID(reqId)는 필수입니다.")
    private String reqId;
    /** 선정여부: Y(선정), N(미선정), R(예비) */
    @NotBlank(message = "선정여부(resultGb)는 필수입니다.")
    private String resultGb;
}
