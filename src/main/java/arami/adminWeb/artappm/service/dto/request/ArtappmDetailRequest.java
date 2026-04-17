package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 지원사업 신청 상세 조회 요청 DTO.
 * reqId가 있으면 REQ_ID(PK)로 조회, 없으면 복합키(PRO_ID, PRO_SEQ, REQ_ESNTL_ID)로 조회.
 */
@Data
public class ArtappmDetailRequest {

    /** 지원사업신청ID (PK). 있으면 주 조회 조건으로 사용 */
    private String reqId;
    private String proId;
    private String proSeq;
    private String reqEsntlId;
    /** 상담일자(03 공공형 슬롯 구분용). 값이 있으면 WORK_DT까지 함께 매칭 */
    private String workDt;
    /** 수정/삭제 시 변경자 ID (CHG_USER_ID) */
    private String UNIQ_ID;
}
