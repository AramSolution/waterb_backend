package arami.logs.service;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserActivityLogDTO {
    private String userId;           // 사용자 ID
    private String sessionId;        // 세션 ID
    private String activityType;     // 활동 타입 (LOGIN, PAGE_VIEW, LOGOUT)
    private String requestUrl;       // 요청 URL
    private String requestMethod;    // HTTP 메소드 (GET, POST 등)
    private String ipAddress;        // IP 주소
    private String userAgent;        // 브라우저 정보
    private LocalDateTime timestamp; // 시간
    private String referer;          // 이전 페이지
    private Long responseTime;       // 응답 시간 (ms)
    private String queryString;      // 쿼리 파라미터
    private String requestBody;      // 요청 본문 (POST/PUT 등)
}