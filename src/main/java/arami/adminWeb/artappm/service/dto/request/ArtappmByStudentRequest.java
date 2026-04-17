package arami.adminWeb.artappm.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 지원사업 신청 - 학생·사업별 최신 건 조회 요청 (userWeb bizInput용)
 */
@Data
public class ArtappmByStudentRequest {

    /** 지원사업 코드 (PRO_ID) */
    private String proId;
    /** 신청자(학생) 고유ID (REQ_ESNTL_ID) */
    private String reqEsntlId;
    /** 학부모 고유ID (P_ESNTL_ID) - 보안 검증용 */
    @JsonProperty("pEsntlId")
    private String pEsntlId;
}
