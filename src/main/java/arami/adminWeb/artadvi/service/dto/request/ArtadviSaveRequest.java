package arami.adminWeb.artadvi.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 상담관리(ARTADVI) 등록/수정 요청.
 * insert: STTUS_CODE 컬럼은 DB 호환용으로 빈 문자열로 저장(앱에서 미사용).
 * insert: REQ_ID, PRO_ID, PRO_SEQ, REQ_ESNTL_ID, ADV_ESNTL_ID, CHG_USER_ID.
 * update: REQ_ID, ADV_ESNTL_ID, ADV_DT, ADV_FROM, ADV_TO, ADV_SPACE, ADV_DESC, FILE_ID, CHG_USER_ID.
 */
@Data
public class ArtadviSaveRequest {
    /** ARTADVI PK (신청 요청 ID) */
    private String reqId;
    private String proId;
    private String proSeq;
    private String reqEsntlId;
    @JsonProperty("advEsntlId")
    private String advEsntlId;
    /** 상담일 (update 시 사용; 멘토 변경 시 '' 권장) */
    private String advDt;
    /** 상담 시작 시각 (update 시 사용) */
    private String advFrom;
    /** 상담 종료 시각 (update 시 사용) */
    private String advTo;
    /** 상담 장소 (update 시 사용) */
    private String advSpace;
    /** 상담 내용 (update 시 사용) */
    private String advDesc;
    /** 첨부파일 그룹 ID (update 시 사용) */
    private String fileId;
    /** 변경자 ID (로그인 사용자) */
    private String chgUserId;
}
