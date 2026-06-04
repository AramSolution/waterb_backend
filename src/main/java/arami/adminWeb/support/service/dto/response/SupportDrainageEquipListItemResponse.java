package arami.adminWeb.support.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipListItemResponse {

    private String itemId;
    private Integer seq;
    private String paySta;
    private String reqDate;

    private String userNm;
    private String zip;
    private String adresLot;
    private String adres;
    private String detailAdres;

    private Integer equipCost;
    private Integer equipPay;

    private String payDay;
    private Integer pay;
}
