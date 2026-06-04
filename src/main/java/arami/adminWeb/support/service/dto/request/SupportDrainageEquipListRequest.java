package arami.adminWeb.support.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 배수설비 관리 목록 조회 요청.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipListRequest {

    /** 등록일 시작(yyyy-MM-dd) */
    private String reqDateFrom;

    /** 등록일 종료(yyyy-MM-dd) */
    private String reqDateTo;

    /** 성명 */
    private String userNm;

    /** 주소 검색(ZIP, ADRES_LOT, ADRES, DETAIL_ADRES 대상) */
    private String address;

    /** 납부상태 필터. 미입력·{@code 00} = 전체, {@code 01} = 미납, {@code 02} = 완납 */
    private String paySta;

    private Integer startIndex;
    private Integer lengthPage;
    private Integer start;
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
