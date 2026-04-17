package arami.userWeb.artprom.service.dto.request;

import java.util.List;

import lombok.Data;

/**
 * 지원사업 목록 조회 요청 DTO (사용자웹)
 */
@Data
public class ArtpromUserListRequest {

    private Integer start;
    private Integer length;
    private Integer startIndex;
    private Integer lengthPage;
    private String searchRecFromDd;
    private String searchRecToDd;
    private String searchProGb;
    private String searchRunSta;
    /** 사업대상 코드(EDR003 상세코드, 예: E1=초등1학년) - 있으면 해당 대상만 필터 */
    private String proTarget;
    /** 학교급 필터: E=초등(전학년), J=중등(전학년), H=고등(전학년). 있으면 해당 학교급 전체 지원사업 조회 */
    private String schoolGb;

    /**
     * REQ_GB 신청대상 위치 필터 (1=학생, 2=학부모, 3=학원, 4=멘토, 5=학교).
     * 로그인 사용자 userSe에 따라 서비스에서 설정. null이면 REQ_GB 조건 미적용.
     */
    private Integer reqGbPosition;

    /**
     * PRO_PART 사업분야 위치 필터 (1=교육/학습, 2=진로/진학, 3=문화/예술/체험, 4=복지/장학, 5=기타).
     * 해당 위치 값이 Y인 건만 조회.
     */
    private String proPartPosition;

    /**
     * 사업유형 필터 — ARTPROM.PRO_TYPE 다중 선택 (01=교육사업, 03=교육외사업).
     * 비어 있거나 null이면 유형 구분 없이 전체 조회.
     */
    private List<String> listProTypes;

    /**
     * 교육목록 진행상태 필터 — ARTPROM.RUN_STA 다중 선택 (01=접수예정, 02=접수중, 04=접수마감, 99=취소).
     * 비어 있거나 null이면 진행상태 구분 없이 전체 조회.
     */
    private List<String> listRunStas;

    /**
     * 검색어 필터 — ARTPROM.PRO_NM LIKE CONCAT('%', #{searchWord}, '%')
     */
    private String searchWord;

    /**
     * 로그인 사용자 고유ID(ESNTL_ID). 서비스에서 설정.
     * 목록 응답의 즐겨찾기 여부 산출(ARTMARK 매핑)에 사용.
     */
    private String esntlId;

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
