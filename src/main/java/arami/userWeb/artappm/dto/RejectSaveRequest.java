package arami.userWeb.artappm.dto;

import lombok.Data;

/**
 * 사용자웹 멘토일지 반려 저장 요청.
 * ARTAPPM STTUS_CODE=11(반려) + REA_DESC 저장. 해당 건 배정 멘토만 호출 가능.
 */
@Data
public class RejectSaveRequest {
    /** ARTAPPM REQ_ID (신청 요청 ID) */
    private String reqId;
    /** 반려 사유 (REA_DESC) */
    private String reaDesc;
}
