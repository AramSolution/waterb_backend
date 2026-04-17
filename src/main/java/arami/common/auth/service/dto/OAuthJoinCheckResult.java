package arami.common.auth.service.dto;

import lombok.Data;

@Data
public class OAuthJoinCheckResult {
    private String esntlId;
    private String userId;
    private String userSe;
    private String mberSttus;

    private String oauthGb;
    private String naverAuthId;
    private String kakaoAuthId;
}

