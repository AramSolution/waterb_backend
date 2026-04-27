package arami.adminWeb.support.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
