package arami.adminWeb.support.service.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerListItemResponse {

    private String itemId;
    private Integer seq;
    private String paySta;
    private String reqDate;

    private String userNm;
    private String zip;
    private String adresLot;
    private String adres;
    private String detailAdres;

    private BigDecimal waterSum;
    private Integer waterCost;
    private BigDecimal waterVal;

    private String payDay;
    private Integer pay;
}
