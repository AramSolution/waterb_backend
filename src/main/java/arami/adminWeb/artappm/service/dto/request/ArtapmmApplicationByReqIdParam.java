package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 멘토 신청(ARTAPMM) 단건 조회 — PRO_ID + REQ_ID로 특정.
 */
@Data
public class ArtapmmApplicationByReqIdParam {

    private String proId;
    private String reqId;
}
