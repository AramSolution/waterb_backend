package arami.adminWeb.support.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerArtitedPayStaUpdateRequest {

    private String itemId;
    private Integer seq;
    private String paySta;
    private String chgUserId;
}
