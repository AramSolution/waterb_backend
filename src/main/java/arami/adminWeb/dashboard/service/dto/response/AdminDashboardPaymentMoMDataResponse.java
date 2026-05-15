package arami.adminWeb.dashboard.service.dto.response;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 월별 납부·미납 비교 본문 데이터.
 */
@Data
public class AdminDashboardPaymentMoMDataResponse {

	/** 요청 기준월을 서버에서 정규화한 값 ({@code yyyy-MM}). */
	private String baseMonth;

	/** 비교 대상 전월 ({@code yyyy-MM}, 기준월의 직전 달). */
	private String compareMonth;

	// ---------- 납부 (ARTITEP, 납부일 PAY_DAY가 해당 달에 속하는 행) ----------

	/** 당월(기준월) 납부 건수. {@code PAY_DAY}가 당월 구간인 ARTITEP 행 수. */
	private Long currPayCount;

	/** 전월 납부 건수. {@code PAY_DAY}가 전월 구간인 ARTITEP 행 수. */
	private Long prevPayCount;

	/**
	 * 전월 대비 납부 건수 상대 증감률(%), {@code (당월-전월)/전월*100}.
	 * 100을 넘을 수 있다(당월이 전월의 2배 초과이면 100% 초과). 전월 건수가 0이면 {@code null}.
	 */
	private BigDecimal payCountChangePercent;

	/** 당월 납부 금액 합계(원). 당월 구간 ARTITEP의 {@code PAY} 합. */
	private Long currPayAmount;

	/** 전월 납부 금액 합계(원). */
	private Long prevPayAmount;

	/**
	 * 전월 대비 납부 금액 상대 증감률(%), {@code (당월-전월)/전월*100}.
	 * 금액이 크게 늘면 200%, 500% 등으로 표시되는 것이 정상이다. 전월 금액이 0이면 {@code null}.
	 */
	private BigDecimal payAmountChangePercent;

	// ---------- 미납 (ARTITED, 통지일 REQ_DATE가 해당 달, PAY_STA = '01') ----------

	/**
	 * 당월 미납 건수. 통지일이 당월이고 {@code PAY_STA = '01'}인 ARTITED 행 수.
	 */
	private Long currUnpaidCount;

	/** 전월 미납 건수. 조건 동일, 전월 구간. */
	private Long prevUnpaidCount;

	/**
	 * 전월 대비 미납 건수 상대 증감률(%), {@code (당월-전월)/전월*100}. 100 초과 가능. 전월이 0이면 {@code null}.
	 */
	private BigDecimal unpaidCountChangePercent;

	/**
	 * 당월 미납 금액 합계(원).
	 * 행마다 {@code max(WATER_COST - WATER_PAY, 0)} 후 합산(과납분은 0으로 처리).
	 */
	private Long currUnpaidAmount;

	/** 전월 미납 금액 합계(원). 계산 방식 동일. */
	private Long prevUnpaidAmount;

	/**
	 * 전월 대비 미납 금액 상대 증감률(%), {@code (당월-전월)/전월*100}. 100 초과 가능. 전월이 0이면 {@code null}.
	 */
	private BigDecimal unpaidAmountChangePercent;
}
