package arami.adminWeb.support.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 오수 원인자부담금 관리 목록 조회 요청.
 * <p>
 * 검색 조건 + 페이징({@code startIndex}/{@code lengthPage} 또는 DataTables {@code start}/{@code length}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerListRequest {

    /** 통지일 시작(yyyy-MM-dd) */
    private String reqDateFrom;

    /** 통지일 종료(yyyy-MM-dd) */
    private String reqDateTo;

    /** 성명 */
    private String userNm;

    /** 주소 검색(ZIP, ADRES_LOT, ADRES, DETAIL_ADRES 대상) */
    private String address;

    /**
     * 납부상태 필터. 미입력·{@code 00} = 전체, {@code 01} = 미납, {@code 02} = 완납(납부완료).
     */
    private String paySta;

    /** 페이징 시작 위치(0부터). 미입력 시 0 (DataTables 호환: {@code start}) */
    private Integer startIndex;

    /** 페이지당 건수. 미입력 시 15 (DataTables 호환: {@code length}) */
    private Integer lengthPage;

    /** {@code startIndex} 별칭 */
    private Integer start;

    /** {@code lengthPage} 별칭 */
    private Integer length;

    public void setDefaultPaging() {
        if (startIndex == null && start != null) {
            startIndex = start;
        }
        if (lengthPage == null && length != null) {
            lengthPage = length;
        }
        if (startIndex == null || startIndex < 0) {
            startIndex = 0;
        }
        if (lengthPage == null || lengthPage <= 0) {
            lengthPage = 15;
        }
    }
}
