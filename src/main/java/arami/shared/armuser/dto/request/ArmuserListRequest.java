package arami.shared.armuser.dto.request;

import lombok.Data;

/**
 * ARMUSER 목록 조회 요청 DTO (공통 사용자)
 */
@Data
public class ArmuserListRequest {

    private Integer start;
    private Integer length;
    private Integer startIndex;
    private Integer lengthPage;
    /** 사용자 구분 (USR: 관리자회원, GNR: 일반회원 등) */
    private String userSe;
    /** 검색 조건 (1: 이름, 2: 아이디, 3: 휴대폰) */
    private String searchCondition;
    /** 검색어 */
    private String searchKeyword;
    /** 회원 상태 (A: 대기, P: 사용, D: 탈퇴) */
    private String mberSttus;

    public void setDefaultPaging() {
        if (this.start == null) this.start = 0;
        if (this.length == null) this.length = 15;
        this.lengthPage = this.length;
        this.startIndex = this.start;
    }
}
