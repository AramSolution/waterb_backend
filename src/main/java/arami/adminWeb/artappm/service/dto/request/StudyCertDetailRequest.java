package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 수강확인증 상세 조회 요청. reqId 있으면 REQ_ID로 조회, 없으면 복합키(proId, proSeq, reqEsntlId) + ARTFILE.SEQ.
 */
@Data
public class StudyCertDetailRequest {

    /** 지원사업신청 PK. 있으면 REQ_ID로 조회 */
    private String reqId;
    private String proId;
    private String proSeq;
    private String reqEsntlId;
    /** ARTFILE.SEQ (목록에서 선택한 수강확인증 파일 순번) */
    private Integer seq;
}
