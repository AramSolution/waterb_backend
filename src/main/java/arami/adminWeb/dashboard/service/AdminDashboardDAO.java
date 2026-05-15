package arami.adminWeb.dashboard.service;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.adminWeb.dashboard.service.dto.request.AdminDashboardPaymentMoMRequest;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardPaymentMoMQueryRow;

/**
 * Persistence layer for admin dashboard APIs.
 */
@Repository("adminDashboardDAO")
public class AdminDashboardDAO extends EgovAbstractMapper {

	public AdminDashboardPaymentMoMQueryRow selectPaymentMonthOverMonthAgg(AdminDashboardPaymentMoMRequest request) {
		return selectOne("adminDashboardDAO.selectPaymentMonthOverMonthAgg", request);
	}
}
