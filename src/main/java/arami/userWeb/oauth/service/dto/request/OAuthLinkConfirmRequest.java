package arami.userWeb.oauth.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthLinkConfirmRequest {
    @NotBlank
    private String linkToken;
}

