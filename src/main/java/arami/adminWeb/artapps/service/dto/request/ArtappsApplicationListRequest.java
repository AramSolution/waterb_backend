package arami.adminWeb.artapps.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 공부의 명수 신청목록 조회 (ARTAPPM · ARTAPPS REQ_ID 조인).
 */
@Data
public class ArtappsApplicationListRequest {

    private Integer start;
    private Integer length;
    private Integer startIndex;
    private Integer lengthPage;

    /** 사업코드(PRO_ID), 필수 */
    @NotBlank(message = "사업코드(searchProId)는 필수입니다.")
    private String searchProId;

    /** 학생명(신청자명) 부분 검색, 선택 */
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
