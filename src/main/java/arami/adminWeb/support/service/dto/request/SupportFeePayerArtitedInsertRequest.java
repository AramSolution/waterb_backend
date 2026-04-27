package arami.adminWeb.support.service.dto.request;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerArtitedInsertRequest {

    private String itemId;
    private Integer seq;
    private String paySta;
    private String type1;
    private String type2;
    private Date reqDate;
    private Integer baseCost;
    private BigDecimal waterSum;
    private BigDecimal waterVal;
    private Integer waterCost;
    private Integer waterPay;
    private String chgUserId;
}
