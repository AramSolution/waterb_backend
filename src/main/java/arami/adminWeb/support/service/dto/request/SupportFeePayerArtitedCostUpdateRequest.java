package arami.adminWeb.support.service.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerArtitedCostUpdateRequest {

    private String itemId;
    private Integer seq;
    private Integer waterCost;
    private BigDecimal waterVal;
    private String chgUserId;
}
