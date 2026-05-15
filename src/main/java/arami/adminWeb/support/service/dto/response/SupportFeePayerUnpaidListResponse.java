package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 오수 원인자부담금 미납 목록 조회 응답 (페이징).
 */
@Data
public class SupportFeePayerUnpaidListResponse {

	private String result;
	private String message;
	private Integer recordsFiltered;
	private Integer recordsTotal;
	private List<SupportFeePayerListItemResponse> data;
}
