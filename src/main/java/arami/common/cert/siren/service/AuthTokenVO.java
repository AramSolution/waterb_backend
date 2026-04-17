package arami.common.cert.siren.service;

import egovframework.com.cmm.service.EgovProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuthTokenVO {

    @Builder.Default
    private String id = EgovProperties.getProperty("bizsiren.id");
    private String srvNo;
    private String reqNum;

    @Builder.Default
    private String certGb = "H";

    private String retUrl;

    @Builder.Default
    private String verSion = "3";

    private String certDate;
}
