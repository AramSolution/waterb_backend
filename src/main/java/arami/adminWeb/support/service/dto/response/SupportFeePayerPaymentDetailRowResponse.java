package arami.adminWeb.support.service.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerPaymentDetailRowResponse {

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
    private Integer seq2;
    private String payDay;
    private Integer pay;
    private String payDesc;
}
