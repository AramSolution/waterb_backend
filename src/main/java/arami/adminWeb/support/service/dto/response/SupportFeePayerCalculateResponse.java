package arami.adminWeb.support.service.dto.response;

import java.math.BigDecimal;

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
}
