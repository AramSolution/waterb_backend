package arami.adminWeb.support.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerArtitemInsertRequest {

    private String itemId;
    private String userNm;
    private String zip;
    private String adresLot;
    private String adres;
    private String detailAdres;
    private String usrTelno;
    private String chgUserId;
}
