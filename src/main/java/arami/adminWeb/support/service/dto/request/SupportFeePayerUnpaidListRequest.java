package arami.adminWeb.support.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 오수 원인자부담금 미납 목록 조회 요청.
 * <p>
 * {@code baseMonth}(yyyy-MM)에 포함된 <strong>연도</strong> 전체를 통지일({@code REQ_DATE}) 기준으로 조회한다.
 * (예: {@code 2026-05} → 2026-01-01 ~ 2026-12-31)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerUnpaidListRequest {

	/** 기준월 (yyyy-MM). 이 값의 연도로 통지일 범위를 잡는다. */
	@NotBlank
	@Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$")
	private String baseMonth;

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
