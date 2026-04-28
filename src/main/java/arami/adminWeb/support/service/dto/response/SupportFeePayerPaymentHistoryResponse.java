package arami.adminWeb.support.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerPaymentHistoryResponse {

    private Integer seq2;
    private String payDay;
    private Integer pay;
    private String payDesc;
}
