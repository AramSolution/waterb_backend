package arami.logs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityLogService {

    private final ObjectMapper objectMapper;

    public void logActivity(UserActivityLogDTO activityLogDTO) {
        try {
            // JSON 형태로 로그 출력
            String jsonLog = objectMapper.writeValueAsString(activityLogDTO);
            log.info("USER_INFO: {}", jsonLog);
        } catch (Exception e) {
            log.error("Failed to log user activity", e);
        }
    }

}