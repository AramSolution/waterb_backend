package arami.adminWeb.artedum.service.dto.request;

import lombok.Data;

/**
 * 가맹학원(희망사업 신청) 목록 조회 요청 DTO
 */
@Data
public class ArtedumListRequest {

    private Integer start;
    private Integer length;
    private Integer startIndex;
    private Integer lengthPage;
    /** 사용자 구분 (USR, ANR 등) - ARMUSER.USER_SE */
    private String userSe;
    /** 검색 조건 (1: 이름, 2: 아이디, 3: 휴대폰, 4: 과목명) */
    private String searchCondition;
    /** 검색어 */
    private String searchKeyword;
    /** 진행상태(ARTEDUM.RUN_STA) 필터: 01=임시저장, 02=신청, 03=승인, 04=반려, 05=정지, 99=취소 */
    private String runSta;
    /** 교육구분(ARTEDUM.EDU_GB) 필터: 01=마중물스터디, 02=희망스터디 (사용자웹 학원목록 모달에서 지원사업별 필터용) */
    private String eduGb;

    public void setDefaultPaging() {
        if (this.start == null) this.start = 0;
        if (this.length == null) this.length = 15;
        this.lengthPage = this.length;
        this.startIndex = this.start;
    }
}
