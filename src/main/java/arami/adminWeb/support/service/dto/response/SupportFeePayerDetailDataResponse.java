package arami.adminWeb.support.service.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerDetailDataResponse {

    private String itemId;
    private String userNm;
    private String zip;
    private String adresLot;
    private String adres;
    private String detailAdres;
    private String usrTelno;
    private List<SupportFeePayerDetailItemResponse> details = new ArrayList<>();
}
