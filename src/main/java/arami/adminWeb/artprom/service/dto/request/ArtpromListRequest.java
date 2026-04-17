package arami.adminWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * 지원사업 목록 조회 요청 DTO
 * selectArtpromList, selectArtpromListCount 파라미터와 일치.
 */
@Data
public class ArtpromListRequest {

    private Integer start;
    private Integer length;
    private Integer startIndex;
    private Integer lengthPage;
    /** 사업기간 조회 - 시작일(YYYYMMDD). SQL REC_TO_DD >= STR_TO_DATE(#{searchRecFromDd}, '%Y%m%d') */
    private String searchRecFromDd;
    /** 사업기간 조회 - 종료일(YYYYMMDD). SQL REC_FROM_DD <= STR_TO_DATE(#{searchRecToDd}, '%Y%m%d') */
    private String searchRecToDd;
    /** 사업구분 조회. PRO_GB = #{searchProGb} */
    private String searchProGb;
    /** 사업명 조회. PRO_NM LIKE CONCAT('%', #{searchProNm}, '%') */
    private String searchProNm;
    /** 진행상태 조회. RUN_STA = #{searchRunSta} (01=공고, 02=접수중, 03=검토중, 04=진행, 05=완료, 99=취소). ''이면 조회조건 미적용 */
    private String searchRunSta;
    /**
     * true이면 REQ_GB 멘토 구간이 Y인 사업만 조회하고, 사업구분(PRO_GB) 단일 조건은 적용하지 않음.
     * 기본 false.
     */
    private Boolean isMentor = Boolean.FALSE;

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
