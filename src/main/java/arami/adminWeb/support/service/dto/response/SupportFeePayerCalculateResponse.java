package arami.adminWeb.support.service.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerCalculateResponse {

    private String result;
    private String message;
    private String itemId;
    private Integer seq;
    private Integer waterCost;
    private BigDecimal waterVal;
    private BigDecimal waterSum;

    /** 선저장 단계에서 완납 등으로 수정·삭제가 적용되지 않은 detail 목록 */
    private List<SupportFeePayerRegisterSkippedDetailResponse> skippedDetails;
}
