package arami.adminWeb.support.service.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerDetailItemResponse {

    private Integer seq;
    private String paySta;
    private String type1;
    private String type2;
    private String reqDate;
    private Integer baseCost;
    private BigDecimal waterSum;
    private BigDecimal waterVal;
    private Integer waterCost;
    private Integer waterPay;
    private List<SupportFeePayerDetailCalculationResponse> calculations = new ArrayList<>();
}
