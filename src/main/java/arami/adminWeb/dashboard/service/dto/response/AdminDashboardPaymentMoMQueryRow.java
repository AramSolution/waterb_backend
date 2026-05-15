package arami.adminWeb.dashboard.service.dto.response;

import java.math.BigDecimal;

import lombok.Data;

/**
 * {@code selectPaymentMonthOverMonthAgg} 한 건 조회 결과.
 * SQL 컬럼 별칭은 UPPER_SNAKE_CASE, MyBatis가 camelCase 프로퍼티에 매핑한다.
 */
@Data
public class AdminDashboardPaymentMoMQueryRow {

	/** 당월 ARTITEP 납부 건수 (PAY_DAY 기준). */
	private Long currPayCnt;

	/** 전월 ARTITEP 납부 건수. */
	private Long prevPayCnt;

	/** 당월 ARTITEP 납부 금액 합 (PAY 합). */
	private BigDecimal currPayAmt;

	/** 전월 ARTITEP 납부 금액 합. */
	private BigDecimal prevPayAmt;

	/** 당월 ARTITED 미납 건수 (REQ_DATE, PAY_STA='01'). */
	private Long currUnpaidCnt;

	/** 전월 ARTITED 미납 건수. */
	private Long prevUnpaidCnt;

	/** 당월 미납 금액 합 (SUM(GREATEST(WATER_COST-WATER_PAY,0))). */
	private BigDecimal currUnpaidAmt;

	/** 전월 미납 금액 합. */
	private BigDecimal prevUnpaidAmt;
}
