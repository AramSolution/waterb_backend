package arami.adminWeb.support.service.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerCalcRequest {

    /**
     * 계산 행 처리 구분.
     * - I: 신규 등록 (seq2 미지정 권장, 서버 채번)
     * - U: 수정 (기존 seq2 필수)
     * - D: 삭제 (기존 seq2 필수)
     * 미지정 시 호환을 위해 seq2가 있으면 U, 없으면 I로 처리.
     */
    private String rowStatus;

    /**
     * 기존 ARTITEC 행 수정/삭제 시 식별용 번호.
     * 신규(I)에서는 미지정 시 서버가 (ITEM_ID, SEQ) 내 MAX(SEQ2)+1로 채번한다.
     */
    private Integer seq2;

    private Integer floor;

    private String buildId;

    private Integer roomCnt;

    private Integer homeCnt;

    private BigDecimal buildSize;

    private BigDecimal dayVal;

    private String costYn;

    private BigDecimal waterVol;
}
