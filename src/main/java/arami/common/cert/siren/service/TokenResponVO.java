package arami.common.cert.siren.service;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenResponVO {

    private String cryptoTokenId;
    private String integrityValue;
    private String reqInfo;
    private String verSion;

}
