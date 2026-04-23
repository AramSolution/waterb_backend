package arami.logs;

import arami.logs.service.UserActivityLogDTO;
import arami.logs.service.UserActivityLogService;
import egovframework.com.cmm.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor // Lombok 어노테이션으로, final 필드를 사용하는 생성자를 자동으로 생성합니다.
public class UserActivityInterceptor implements HandlerInterceptor {

    private final UserActivityLogService userActivityLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        Long startTime = (Long) request.getAttribute("startTime");
        long responseTime = (startTime != null) ? System.currentTimeMillis() - startTime : 0;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // null 체크 및 사용자 ID 추출
        String userId = "anonymous";
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof LoginVO) {
                userId = ((LoginVO) principal).getId();
            } else if (principal instanceof String) {
                // Anonymous 인증인 경우 "anonymousUser" 문자열이 올 수 있음
                userId = "anonymous";
            } else {
                userId = auth.getName();
            }
        }

        // 세션이 없어도 새로 생성하지 않도록 getSession(false)를 사용합니다.
        HttpSession session = request.getSession(false);
        String sessionId = (session != null) ? session.getId() : "NO_SESSION";

        // 쿼리 파라미터 추출
        String queryString = request.getQueryString();

        // 요청 본문 추출 (이미 읽혔을 수 있으므로 attribute에서 가져옴)
        String requestBody = (String) request.getAttribute("requestBody");

        UserActivityLogDTO activityLog = UserActivityLogDTO.builder()
                .userId(userId)
                .sessionId(sessionId)
                .activityType("PAGE_VIEW")
                .requestUrl(request.getRequestURI())
                .requestMethod(request.getMethod())
                .ipAddress(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .referer(request.getHeader("Referer"))
                .timestamp(LocalDateTime.now())
                .responseTime(responseTime)
                .queryString(queryString)
                .requestBody(requestBody)
                .build();

        userActivityLogService.logActivity(activityLog);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}