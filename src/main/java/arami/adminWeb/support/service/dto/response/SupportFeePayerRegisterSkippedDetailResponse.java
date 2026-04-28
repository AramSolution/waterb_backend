package arami.adminWeb.support.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 등록·수정 요청 배치 처리 시 DB상 완납 등으로 수정/삭제가 적용되지 않은 detail.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerRegisterSkippedDetailResponse {

    /** ARTITED SEQ */
    private Integer seq;

    /** 요청된 detail.rowStatus (U: 수정, D: 삭제) */
    private String requestedRowStatus;

    /** 생략 사유: 완납 건(ARTITED.PAY_STA가 미납 01이 아님) */
    public static final String SKIP_REASON_PAID = "PAID";

    private String skipReason;
}
