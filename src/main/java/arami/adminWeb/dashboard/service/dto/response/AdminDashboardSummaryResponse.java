package arami.adminWeb.dashboard.service.dto.response;

import lombok.Data;

@Data
public class AdminDashboardSummaryResponse {
	private String result;
	private String message;
	private AdminDashboardSummaryDataResponse data;
}
