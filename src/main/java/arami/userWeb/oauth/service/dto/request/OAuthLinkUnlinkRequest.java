package arami.userWeb.oauth.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthLinkUnlinkRequest {
    /**
     * OAuth provider
     * - naver
     * - kakao
     */
    @NotBlank
    private String oauthService;
}

