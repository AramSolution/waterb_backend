package arami.adminWeb.support.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipRegisterSkippedDetailResponse {

    private Integer seq;
    private String requestedRowStatus;
    public static final String SKIP_REASON_PAID = "PAID";
    private String skipReason;
    private Integer seq2;
}
