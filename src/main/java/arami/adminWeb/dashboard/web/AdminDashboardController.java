package arami.adminWeb.dashboard.web;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import arami.adminWeb.dashboard.service.AdminDashboardService;
import arami.adminWeb.dashboard.service.dto.request.AdminDashboardPaymentMoMRequest;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardPaymentMoMResponse;
import arami.adminWeb.dashboard.service.dto.response.AdminDashboardSummaryResponse;
import arami.common.CommonService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController extends CommonService {

	@Resource(name = "adminDashboardService")
	private AdminDashboardService adminDashboardService;

	@Resource(name = "egovMessageSource")
	private EgovMessageSource egovMessageSource;

	/**
	 * Admin dashboard summary (metrics to be wired in service/mapper).
	 */
	@GetMapping(value = "/summary", produces = "application/json;charset=UTF-8")
	public ResponseEntity<AdminDashboardSummaryResponse> summary() {
		AdminDashboardSummaryResponse response = new AdminDashboardSummaryResponse();
		try {
			response = adminDashboardService.selectSummary();
			response.setResult("00");
			response.setMessage(egovMessageSource.getMessage("success.common.select"));
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("admin dashboard summary error: {}", e.getMessage(), e);
			response.setResult("01");
			response.setMessage("대시보드 요약 조회 중 오류가 발생했습니다.");
			if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
				e.printStackTrace();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * 기준월 대비 전월 납부(ARTITEP, PAY_DAY) 건수·금액 및 증감률.
	 */
	@PostMapping(value = "/payment-mom", produces = "application/json;charset=UTF-8")
	public ResponseEntity<AdminDashboardPaymentMoMResponse> paymentMonthOverMonth(
			@RequestBody @Valid AdminDashboardPaymentMoMRequest request) {
		AdminDashboardPaymentMoMResponse response = new AdminDashboardPaymentMoMResponse();
		try {
			response = adminDashboardService.selectPaymentMonthOverMonth(request);
			response.setResult("00");
			response.setMessage(egovMessageSource.getMessage("success.common.select"));
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			log.warn("admin dashboard payment-mom validation: {}", e.getMessage());
			response.setResult("01");
			response.setMessage(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			log.error("admin dashboard payment-mom error: {}", e.getMessage(), e);
			response.setResult("01");
			response.setMessage("납부 월별 비교 조회 중 오류가 발생했습니다.");
			if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
				e.printStackTrace();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
