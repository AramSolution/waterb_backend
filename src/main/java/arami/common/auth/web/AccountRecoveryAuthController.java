package arami.common.auth.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import arami.common.auth.service.AccountRecoveryService;
import arami.common.auth.service.dto.FindUserIdByCrtfcDnRequest;
import arami.common.auth.service.dto.FindUserIdByRecoveryTokenRequest;
import arami.common.auth.service.dto.PasswordResetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 웹 로그인 — 아이디 찾기(본인인증 DI 또는 recoveryToken).
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "AccountRecoveryAuth", description = "아이디 찾기(본인인증)")
public class AccountRecoveryAuthController {

    private final AccountRecoveryService accountRecoveryService;

    @Operation(summary = "아이디 찾기 — DI + 회원유형")
    @PostMapping("/auth/find-user-id")
    public ResponseEntity<Map<String, Object>> findUserIdByCrtfcDn(
            @Valid @RequestBody FindUserIdByCrtfcDnRequest request) throws Exception {
        return ResponseEntity.ok(accountRecoveryService.findUserIdByCrtfcDn(request));
    }

    @Operation(summary = "아이디 찾기 — 본인인증 recoveryToken(JWT)")
    @PostMapping("/auth/account-recovery/find-user-id")
    public ResponseEntity<Map<String, Object>> findUserIdByRecoveryToken(
            @Valid @RequestBody FindUserIdByRecoveryTokenRequest request) throws Exception {
        return ResponseEntity.ok(accountRecoveryService.findUserIdByRecoveryToken(request));
    }

    @Operation(summary = "비밀번호 재설정 — recoveryToken 또는 crtfcDnValue + 새 비밀번호")
    @PostMapping("/auth/password-reset/request")
    public ResponseEntity<Map<String, Object>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request) throws Exception {
        return ResponseEntity.ok(accountRecoveryService.requestPasswordReset(request));
    }
}
