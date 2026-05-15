package arami.adminWeb.dashboard.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관리자 대시보드 월별 납부·미납 비교 API 요청.
 * <p>
 * {@code baseMonth} 기준으로 당월과 그 직전 월을 비교한다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardPaymentMoMRequest {

	/**
	 * 기준월. 형식 {@code yyyy-MM} (예: 2026-05).
	 * <ul>
	 *   <li>당월: 이 월의 1일부터 말일까지</li>
	 *   <li>전월: 기준월 직전 달의 동일한 달력 구간</li>
	 * </ul>
	 */
	@NotBlank
	@Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$")
	private String baseMonth;
}
