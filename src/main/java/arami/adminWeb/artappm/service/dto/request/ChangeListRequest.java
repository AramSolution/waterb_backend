package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 변경이력 조회 요청 (f_changlist 프로시저 인자).
 * aGubun: 01 고정(artappm), proId, proSeq, reqEsntlId.
 */
@Data
public class ChangeListRequest {

    /** 구분 (01: artappm) */
    private String aGubun;
    private String proId;
    private String proSeq;
    private String reqEsntlId;
}
