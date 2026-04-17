package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 멘토 신청(ARTAPMM) 목록 조회 — 사업(PRO_ID) 단위, 멘토명(USER_NM) 검색, 페이징.
 */
@Data
public class ArtapmmApplicationListRequest {

    private Integer start;
    private Integer length;
    private Integer startIndex;
    private Integer lengthPage;

    /** 지원사업코드 (필수) */
    private String searchProId;

    /** 멘토명 (ARMUSER.USER_NM, LIKE) */
    private String searchUserNm;

    public void setDefaultPaging() {
        if (this.start == null) {
            this.start = 0;
        }
        if (this.length == null) {
            this.length = 15;
        }
        this.lengthPage = this.length;
        this.startIndex = this.start;
    }
}
