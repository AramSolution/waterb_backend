package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 멘토 저장 요청 한 건 (ARTAPMM).
 * reqId 있으면 UPDATE, 없으면 INSERT.
 */
@Data
public class ArtapmmMentorItemSaveRequest {

    /** 기존 멘토 REQ_ID (있으면 UPDATE, 없으면 INSERT) */
    private String reqId;
    /** 멘토(신청자) ESNTL_ID */
    private String reqEsntlId;
    /** 선정여부 (Y/N/R) */
    private String resultGb;
    /** 상태 (A/D 등) */
    private String sttusCode;
}
