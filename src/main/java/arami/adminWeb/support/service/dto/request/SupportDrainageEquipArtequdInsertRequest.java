package arami.adminWeb.support.service.dto.request;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipArtequdInsertRequest {

    private String itemId;
    private Integer seq;
    private String paySta;
    private Date reqDate;
    private Date startDate;
    private Date planDate;
    private Date compDate;
    private String agency;
    private Integer equipCost;
    private Integer equipPay;
    private String chgUserId;
}
