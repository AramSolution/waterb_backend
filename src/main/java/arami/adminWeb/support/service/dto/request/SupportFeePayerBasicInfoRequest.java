package arami.adminWeb.support.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerBasicInfoRequest {

    @NotBlank
    private String userNm;

    private String zip;

    private String adresLot;

    @NotBlank
    private String adres;

    private String detailAdres;

    @NotBlank
    private String usrTelno;
}
