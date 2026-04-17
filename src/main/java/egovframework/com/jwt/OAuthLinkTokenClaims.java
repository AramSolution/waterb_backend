package egovframework.com.jwt;

import lombok.Data;

@Data
public class OAuthLinkTokenClaims {
    private String email;
    private String userSe;
    private String oauthService;
    private String oauthGb;
    private String oauthAuthId;
    private String state;
}

