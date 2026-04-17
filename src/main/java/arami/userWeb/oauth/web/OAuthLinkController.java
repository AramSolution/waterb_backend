package arami.userWeb.oauth.web;

import arami.userWeb.oauth.service.OAuthLinkService;
import arami.userWeb.oauth.service.dto.request.OAuthLinkConfirmRequest;
import arami.userWeb.oauth.service.dto.request.OAuthLinkUnlinkRequest;
import arami.userWeb.oauth.service.dto.response.OAuthLinkConfirmResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/userWeb/oauth-link")
@RequiredArgsConstructor
public class OAuthLinkController {

    private final OAuthLinkService oAuthLinkService;

    @PostMapping("/confirm")
    public ResponseEntity<OAuthLinkConfirmResponse> confirm(@RequestBody @Valid OAuthLinkConfirmRequest request) {
        return ResponseEntity.ok(oAuthLinkService.confirmLink(request));
    }

    @PostMapping("/unlink")
    public ResponseEntity<Void> unlink(@RequestBody @Valid OAuthLinkUnlinkRequest request) {
        oAuthLinkService.unlinkLink(request);
        return ResponseEntity.noContent().build();
    }
}

