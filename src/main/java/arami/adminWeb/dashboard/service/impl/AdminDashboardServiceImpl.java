package arami.adminWeb.dashboard.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import arami.adminWeb.dashboard.service.AdminDashboardDAO;
import arami.adminWeb.dashboard.service.AdminDashboardService;
import arami.adminWeb.dashboard.service.dto.request.AdminDashboardPaymentMoMRequest;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardPaymentMoMDataResponse;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardPaymentMoMQueryRow;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardPaymentMoMResponse;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardSummaryDataResponse;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardSummaryResponse;

@Service("adminDashboardService")
public class AdminDashboardServiceImpl extends EgovAbstractServiceImpl implements AdminDashboardService {

	@Resource(name = "adminDashboardDAO")
	private AdminDashboardDAO adminDashboardDAO;

	@Override
	public AdminDashboardSummaryResponse selectSummary() throws Exception {
		AdminDashboardSummaryResponse response = new AdminDashboardSummaryResponse();
		response.setData(new AdminDashboardSummaryDataResponse());
		return response;
	}

	@Override
	public AdminDashboardPaymentMoMResponse selectPaymentMonthOverMonth(AdminDashboardPaymentMoMRequest request)
			throws Exception {
		String baseMonth = request.getBaseMonth() == null ? "" : request.getBaseMonth().trim();
		YearMonth ym;
		try {
			ym = YearMonth.parse(baseMonth);
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("기준월 형식이 올바르지 않습니다.", ex);
		}
		String normalizedBase = ym.toString();
		String compareMonth = ym.minusMonths(1).toString();

		AdminDashboardPaymentMoMRequest queryParam = new AdminDashboardPaymentMoMRequest();
		queryParam.setBaseMonth(normalizedBase);

		AdminDashboardPaymentMoMQueryRow row = adminDashboardDAO.selectPaymentMonthOverMonthAgg(queryParam);
		long currCnt = toLong(row != null ? row.getCurrPayCnt() : null);
		long prevCnt = toLong(row != null ? row.getPrevPayCnt() : null);
		long currAmt = toAmountLong(row != null ? row.getCurrPayAmt() : null);
		long prevAmt = toAmountLong(row != null ? row.getPrevPayAmt() : null);
		long currUnpaidCnt = toLong(row != null ? row.getCurrUnpaidCnt() : null);
		long prevUnpaidCnt = toLong(row != null ? row.getPrevUnpaidCnt() : null);
		long currUnpaidAmt = toAmountLong(row != null ? row.getCurrUnpaidAmt() : null);
		long prevUnpaidAmt = toAmountLong(row != null ? row.getPrevUnpaidAmt() : null);

		AdminDashboardPaymentMoMDataResponse data = new AdminDashboardPaymentMoMDataResponse();
		data.setBaseMonth(normalizedBase);
		data.setCompareMonth(compareMonth);
		data.setCurrPayCount(currCnt);
		data.setPrevPayCount(prevCnt);
		data.setPayCountChangePercent(percentChange(currCnt, prevCnt));
		data.setCurrPayAmount(currAmt);
		data.setPrevPayAmount(prevAmt);
		data.setPayAmountChangePercent(percentChange(currAmt, prevAmt));
		data.setCurrUnpaidCount(currUnpaidCnt);
		data.setPrevUnpaidCount(prevUnpaidCnt);
		data.setUnpaidCountChangePercent(percentChange(currUnpaidCnt, prevUnpaidCnt));
		data.setCurrUnpaidAmount(currUnpaidAmt);
		data.setPrevUnpaidAmount(prevUnpaidAmt);
		data.setUnpaidAmountChangePercent(percentChange(currUnpaidAmt, prevUnpaidAmt));

		AdminDashboardPaymentMoMResponse response = new AdminDashboardPaymentMoMResponse();
		response.setData(data);
		return response;
	}

	private static long toLong(Long value) {
		return value != null ? value : 0L;
	}

	private static long toAmountLong(BigDecimal value) {
		if (value == null) {
			return 0L;
		}
		return value.setScale(0, RoundingMode.HALF_UP).longValue();
	}

	/**
	 * 전월 대비 상대 증감률(%), 소수 둘째 자리 반올림.
	 * <p>
	 * 공식: {@code (당월 - 전월) / 전월 * 100}. 전월을 100% 기준(분모)으로 잡은 비율 변화이므로
	 * 상한이 100이 아니다. 예: 전월 3건·당월 6건 → +100%;
	 * 전월 대비 당월이 2배가 넘으면 100%를 초과한다(예: 금액 약 3.25배 → 약 +225%).
	 * <p>
	 * 전월이 0이면 분모가 없어 {@code null}을 반환한다.
	 */
	private static BigDecimal percentChange(long current, long previous) {
		if (previous == 0) {
			return null;
		}
		return BigDecimal.valueOf(current - previous)
				.multiply(BigDecimal.valueOf(100))
				.divide(BigDecimal.valueOf(previous), 2, RoundingMode.HALF_UP);
	}
}
