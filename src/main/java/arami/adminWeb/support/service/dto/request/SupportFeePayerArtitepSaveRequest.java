package arami.adminWeb.support.service.dto.request;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerArtitepSaveRequest {

    private String itemId;
    private Integer seq;
    private Integer seq2;
    private Date payDay;
    private Integer pay;
    private String payDesc;
    private String chgUserId;
}
