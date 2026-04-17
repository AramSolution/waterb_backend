package arami.shared.armuser.dto.request;

import lombok.Data;

/**
 * 사용자웹 학원조회 목록 요청 DTO
 * 정상상태(ANR, P) 학원 전부 + 과목(ARTEDUD) 콤마 구분
 */
@Data
public class AcademyListForUserRequest {

    private Integer start;
    private Integer length;
    private Integer startIndex;
    private Integer lengthPage;
    /** 검색 조건 (1: 학원명) */
    private String searchCondition;
    /** 검색어 */
    private String searchKeyword;

    public void setDefaultPaging() {
        if (this.start == null) this.start = 0;
        if (this.length == null) this.length = 100;
        this.lengthPage = this.length;
        this.startIndex = this.start;
    }
}
