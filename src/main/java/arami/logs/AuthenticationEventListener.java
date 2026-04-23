package arami.logs;

import arami.logs.service.UserActivityLogDTO;
import arami.logs.service.UserActivityLogService;
import egovframework.com.cmm.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final UserActivityLogService logService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return;

        String userId = event.getAuthentication().getName();
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof LoginVO) {
            userId = ((LoginVO) principal).getId();
        }

        UserActivityLogDTO activityLog = UserActivityLogDTO.builder()
                .userId(userId)
                .sessionId(request.getSession().getId())
                .activityType("LOGIN")
                .requestUrl("/auth/login-jwt")
                .requestMethod("POST")
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .timestamp(LocalDateTime.now())
                .build();

        logService.logActivity(activityLog);
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return;

        String userId = event.getAuthentication().getName();
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof LoginVO) {
            userId = ((LoginVO) principal).getId();
        }

        UserActivityLogDTO activityLog = UserActivityLogDTO.builder()
                .userId(userId)
                .sessionId(request.getSession().getId())
                .activityType("LOGOUT")
                .requestUrl("/logout")
                .requestMethod("POST")
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .timestamp(LocalDateTime.now())
                .build();

        logService.logActivity(activityLog);
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

}