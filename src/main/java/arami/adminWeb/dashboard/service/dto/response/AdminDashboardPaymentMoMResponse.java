package arami.adminWeb.dashboard.service.dto.response;

import lombok.Data;

/**
 * 관리자 대시보드 월별 납부·미납 비교 API 최상위 응답.
 */
@Data
public class AdminDashboardPaymentMoMResponse {

	/**
	 * 처리 결과 코드. 성공 {@code 00}, 실패 {@code 01}.
	 */
	private String result;

	/**
	 * 결과 메시지. 성공 시 조회 성공 메시지(메시지소스), 실패 시 오류 안내 문구.
	 */
	private String message;

	/**
	 * 당월·전월 납부(ARTITEP) 및 미납(ARTITED) 집계와 전월 대비 증감률.
	 */
	private AdminDashboardPaymentMoMDataResponse data;
}
