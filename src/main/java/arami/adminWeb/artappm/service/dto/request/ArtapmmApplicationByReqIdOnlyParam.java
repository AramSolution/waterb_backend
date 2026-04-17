package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 멘토 신청(ARTAPMM) 단건 조회 — REQ_ID만으로 특정 (사용자웹 MY PAGE 상세).
 */
@Data
public class ArtapmmApplicationByReqIdOnlyParam {

    private String reqId;
}
