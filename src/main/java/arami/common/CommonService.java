package arami.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import egovframework.com.cmm.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import jakarta.annotation.Resource;

@Slf4j
public class CommonService {

    /** 암호화 유틸리티 */
	@Resource
	private CryptoUtil cryptoUtil;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public void setCommon(HttpServletRequest request, ModelMap model) throws Exception {
        model.putAll(Util.getParameterModelMap(request));
        
        // JSON body 처리 (Content-Type이 application/json인 경우)
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            try {
                String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                if (body != null && !body.trim().isEmpty()) {
                    Map<String, Object> jsonMap = objectMapper.readValue(body, Map.class);
                    
                    // cryptoUtil.decryptPersonalInfoMap(jsonMap);

                    model.putAll(jsonMap);
                }
            } catch (Exception e) {
                log.warn("JSON body 파싱 실패: " + e.getMessage());
            }
        }

        
        // LIMIT OFFSET 페이징처리(DataTable) - null 체크 추가
        if(model.get("start") == null) {
            model.put("start", 0);
        }

        if(model.get("length") == null) {
            model.put("length", 15);
        }

        
        // LIMIT OFFSET 페이징처리(DataTable)
        model.put("lengthPage", Integer.parseInt(model.get("length").toString()));
        model.put("startIndex" , Integer.parseInt(model.get("start").toString()));
        
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        model.put("contextPath", request.getContextPath());
        
        if (authentication != null && authentication.getPrincipal() instanceof LoginVO) {
            LoginVO loginVO = (LoginVO) authentication.getPrincipal();

            log.info("로그인 사용자 ID: " + loginVO.getId());
            log.info("로그인 사용자 이름: " + loginVO.getName());
            log.info("로그인 사용자 권한: " + loginVO.getGroupNm());
        } else {
            log.error("인증 정보가 올바르지 않습니다. Principal type: " + (authentication != null ? authentication.getPrincipal().getClass().getName() : "null"));
        }

    }

    /**
     * 현재 로그인 사용자의 uniqId 반환 (파일 CHG_USER_ID 등에 사용).
     * @return 로그인 사용자 uniqId, 없으면 빈 문자열
     */
    protected String getCurrentUniqId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginVO) {
            LoginVO loginVO = (LoginVO) authentication.getPrincipal();
            return loginVO.getUniqId() != null ? loginVO.getUniqId() : "";
        }
        return "";
    }

    /**
     * 현재 로그인 사용자 구분(예: SNR, PNR, MNR). 없으면 빈 문자열.
     */
    protected String getCurrentUserSe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginVO) {
            LoginVO loginVO = (LoginVO) authentication.getPrincipal();
            return loginVO.getUserSe() != null ? loginVO.getUserSe() : "";
        }
        return "";
    }

}