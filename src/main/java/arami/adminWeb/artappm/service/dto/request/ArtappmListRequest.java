package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 지원사업 신청 목록 조회 요청 DTO
 */
@Data
public class ArtappmListRequest {

    private Integer start;
    private Integer length;
    private Integer startIndex;
    private Integer lengthPage;
    /** 지원사업코드 */
    private String searchProId;
    /** 신청자명 */
    private String searchUserNm;
    /** 회차 */
    private String searchProSeq;
    /** 신청자 ID */
    private String searchReqEsntlId;
    /** 지원사업 신청 REQ_ID (수강확인증 목록/엑셀/카운트 시 이 값이 있으면 REQ_ID로만 조회) */
    private String searchReqId;
    /** 상태 */
    private String searchSttusCode;
    /** 선정여부 */
    private String searchResultGb;

    public void setDefaultPaging() {
        if (this.start == null) this.start = 0;
        if (this.length == null) this.length = 15;
        this.lengthPage = this.length;
        this.startIndex = this.start;
    }
}
