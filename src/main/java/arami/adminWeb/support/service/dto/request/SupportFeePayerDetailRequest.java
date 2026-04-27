package arami.adminWeb.support.service.dto.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerDetailRequest {

    /**
     * 행 처리 구분.
     * - I: 신규 등록 (seq 미지정 권장, 서버 채번)
     * - U: 수정 (기존 seq 필수)
     * - D: 삭제 (기존 seq 필수)
     * 미지정 시 호환을 위해 seq가 있으면 U, 없으면 I로 처리.
     */
    private String rowStatus;

    /**
     * 기존 ARTITED 행 대상으로 수정/삭제 시 해당 SEQ를 지정.
     * 신규(I)에서는 미지정 시 서버가 ITEM_ID 내 MAX(SEQ)+1로 채번한다.
     */
    private Integer seq;

    private String paySta;

    private String type1;

    private String type2;

    private String reqDate;

    private Integer baseCost;

    private BigDecimal waterSum;

    @Valid
    private List<SupportFeePayerCalcRequest> calculations = new ArrayList<>();
}
