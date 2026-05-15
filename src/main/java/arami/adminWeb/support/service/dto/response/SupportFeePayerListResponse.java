package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 오수 원인자부담금 관리 목록 조회 응답 (페이징).
 */
@Data
public class SupportFeePayerListResponse {

    private String result;
    private String message;

    /** 검색 조건 적용 후 전체 건수 */
    private Integer recordsFiltered;

    /** 전체 건수 (목록 API에서는 recordsFiltered와 동일) */
    private Integer recordsTotal;

    /** 현재 페이지 목록 */
    private List<SupportFeePayerListItemResponse> data;
}
