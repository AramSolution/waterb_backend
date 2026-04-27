package arami.adminWeb.support.service.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerArtitecInsertRequest {

    private String itemId;
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
    private String chgUserId;
}
