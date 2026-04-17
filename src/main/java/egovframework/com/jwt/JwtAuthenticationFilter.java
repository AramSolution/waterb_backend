package egovframework.com.jwt;

import egovframework.com.cmm.LoginVO;
import egovframework.let.utl.fcc.service.EgovStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * fileName       : JwtAuthenticationFilter
 * author         : crlee
 * date           : 2023/06/11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/06/11        crlee       최초 생성
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private EgovJwtTokenUtil jwtTokenUtil;
    public static final String HEADER_STRING = "Authorization";

    @Override //로그인 이후 HttpServletRequest 요청할 때마다 실행(스프링의 AOP기능)
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        // step 1. request header에서 토큰을 가져온다.
        String jwtToken = EgovStringUtil.isNullToString(req.getHeader(HEADER_STRING));

        logger.debug("===>>> Request URI: " + req.getRequestURI());
        logger.debug("===>>> Authorization Header Raw: [" + jwtToken + "]");

        // 토큰 정리: 공백 제거 및 Bearer 접두사 제거
        if (jwtToken != null && !jwtToken.isEmpty()) {
            jwtToken = jwtToken.trim(); // 앞뒤 공백 제거

            // "Bearer " 접두사가 있으면 제거
            if (jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7).trim();
                logger.debug("===>>> Removed 'Bearer ' prefix from token");
            }

            logger.debug("===>>> Token after cleanup (length: " + jwtToken.length() + ")");
        }

        // step 2. 토큰에 내용이 있는지 확인해서 id값을 가져옴
        // Exception 핸들링 추가처리 (토큰 유효성, 토큰 변조 여부, 토큰 만료여부)
        // 내부적으로 parse하는 과정에서 해당 여부들이 검증됨
        if (jwtToken != null && !jwtToken.isEmpty()) {
            try {
                LoginVO loginVO = jwtTokenUtil.getLoginVOFromToken(jwtToken);
                logger.debug("===>>> id = " + loginVO.getId());
                logger.debug("===>>> loginVO.getUserSe() = " + loginVO.getUserSe());
                logger.debug("===>>> loginVO.getGroupNm() = " + loginVO.getGroupNm());

                String role = isAdmin(loginVO) ? "ROLE_ADMIN" : "ROLE_USER";
                logger.debug("===>>> Assigned Role: " + role);


                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        loginVO, null, Arrays.asList(new SimpleGrantedAuthority(role))
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("===>>> Authentication Success: " + authentication);
                logger.debug("===>>> Authorities: " + authentication.getAuthorities());
            } catch (InvalidJwtException e) {
                logger.error("===>>> JWT Token Validation Failed: " + e.getMessage());
                logger.error("===>>> Token causing error (first 20 chars): " +
                    (jwtToken.length() > 20 ? jwtToken.substring(0, 20) + "..." : jwtToken));
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                logger.error("===>>> Unexpected error during JWT processing: " + e.getMessage());
                logger.error("===>>> Token causing error (first 20 chars): " +
                    (jwtToken.length() > 20 ? jwtToken.substring(0, 20) + "..." : jwtToken));
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.debug("===>>> No JWT Token provided");
        }

        chain.doFilter(req, res);
    }

    private boolean isAdmin(LoginVO loginVO) {
        return "ROLE_ADMIN".equals(loginVO.getGroupNm());
    }
}