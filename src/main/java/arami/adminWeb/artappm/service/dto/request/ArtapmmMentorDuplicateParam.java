package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * ARTAPMM 동일 사업·회차·멘토(신청자) 활성 건 존재 여부 조회용.
 */
@Data
public class ArtapmmMentorDuplicateParam {

    private String proId;
    private Integer proSeq;
    private String reqEsntlId;

    /** 중복 검사 시 제외할 REQ_ID (수정 시 본인 건) */
    private String excludeReqId;
}
