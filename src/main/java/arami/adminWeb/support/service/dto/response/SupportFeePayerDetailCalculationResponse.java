package arami.adminWeb.support.service.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerDetailCalculationResponse {

    private Integer seq;
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
