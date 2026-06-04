package arami.adminWeb.support.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipDetailItemResponse {

    private Integer seq;
    private String paySta;
    private String reqDate;
    private String startDate;
    private String planDate;
    private String compDate;
    private String agency;
    private Integer equipCost;
    private Integer equipPay;
}
