package arami.adminWeb.support.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipPaymentRequest {

    private String rowStatus;
    private Integer seq2;
    private String payDay;
    private Integer pay;
    private String payDesc;
}
