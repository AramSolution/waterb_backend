package arami.common.cert.siren.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RequestCertVO {
    private String srvNo;
    private String retUrl;
}
