package arami.common.cert.comm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MobileCertDTO {

    private String reqSeq;
    private String authType;
    private String cipherTime;
    private String resSeq;
    private String name;
    private String birthDate;
    private String gender;
    private String nationaInfo;
    private String dupInfo;
    private String connInfo;
    private String mobileCo;
    private String mobileNo;
    private String errCode;
    private String errMsg;
    private String chgDate;

}
