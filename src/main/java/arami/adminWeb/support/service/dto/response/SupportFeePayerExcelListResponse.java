package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 오수 원인자부담금 엑셀 목록 조회 응답 DTO.
 */
@Data
public class SupportFeePayerExcelListResponse {

    /** 엑셀용 목록 데이터 */
    private List<SupportFeePayerListItemResponse> data;

    /** 결과 코드 ("00": 성공, "01": 실패) */
    private String result;
}
