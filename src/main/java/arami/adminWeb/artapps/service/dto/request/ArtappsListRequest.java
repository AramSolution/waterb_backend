package arami.adminWeb.artapps.service.dto.request;

import lombok.Data;

/**
 * 공부의명수 목록 조회 요청 DTO  
 * selectArtappsList, selectArtappsListCount 파라미터와 일치.
 */
@Data
public class ArtappsListRequest {

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
    /** 진행상태 조회. RUN_STA = #{searchRunSta} (01=공고, 02=접수중, 03=검토중, 04=진행, 05=완료, 99=취소). ''이면 조회조건 미적용 */
    private String searchRunSta;

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
