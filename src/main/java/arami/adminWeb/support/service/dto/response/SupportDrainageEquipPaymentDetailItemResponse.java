package arami.adminWeb.support.service.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipPaymentDetailItemResponse {

    private Integer seq;
    private String paySta;
    private String reqDate;
    private String startDate;
    private String planDate;
    private String compDate;
    private String agency;
    private Integer equipCost;
    private Integer equipPay;
    private List<SupportDrainageEquipPaymentHistoryResponse> payments = new ArrayList<>();
}
