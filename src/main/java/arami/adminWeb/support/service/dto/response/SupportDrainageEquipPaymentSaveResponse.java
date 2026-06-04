package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipPaymentSaveResponse {

    private String result;
    private String message;
    private String itemId;
    private List<SupportDrainageEquipRegisterSkippedDetailResponse> skippedDetails;
}
