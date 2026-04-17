package arami.userWeb.oauth.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OAuthLinkConfirmResponse {
    private String accessToken;
    private String state;
    /** USER_SE (SNR/PNR/ANR/MNR) */
    private String userSe;
    /** ESNTL_ID (JWT claim: uniqId) */
    private String uniqId;
}

