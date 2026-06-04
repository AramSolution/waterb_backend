package arami.adminWeb.support.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipBasicUpdateResponse {

    private String result;
    private String message;
    private String itemId;
}
