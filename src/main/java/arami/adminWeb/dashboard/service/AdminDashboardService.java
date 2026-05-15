package arami.adminWeb.dashboard.service;

import arami.adminWeb.dashboard.service.dto.request.AdminDashboardPaymentMoMRequest;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardPaymentMoMResponse;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardSummaryResponse;

/**
 * Admin dashboard business operations.
 */
public interface AdminDashboardService {

	/**
	 * Aggregated dashboard summary for the admin home (implementation pending).
	 */
	AdminDashboardSummaryResponse selectSummary() throws Exception;

	/**
	 * 당월·전월 납부(ARTITEP, PAY_DAY 기준) 건수·금액 및 전월 대비 증감률.
	 */
	AdminDashboardPaymentMoMResponse selectPaymentMonthOverMonth(AdminDashboardPaymentMoMRequest request)
			throws Exception;
}
