package egovframework.com.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import arami.common.auth.service.AuthRoleManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.support.MultipartFilter;

import egovframework.com.cmm.filter.HTMLTagFilter;
import egovframework.com.jwt.JwtAuthenticationEntryPoint;
import egovframework.com.jwt.JwtAuthenticationFilter;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.service.ResultVO;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.access.AccessDeniedException;
import jakarta.servlet.ServletException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;

/**
 * fileName : SecurityConfig
 * author : crlee
 * date : 2023/06/10
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2023/06/10 crlee 최초 생성
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthRoleManageService authRoleManageService;

    @Autowired
    private Environment environment;

    /** EgovPropertyService */
    @Resource(name = "propertiesService")
    private EgovPropertyService propertiesService;

    // Http Methpd : Get 인증예외 List
    private String[] AUTH_GET_WHITELIST = {
            "/mainPage", // 메인 화면 리스트 조회
            "/board", // 게시판 목록조회
            "/board/{bbsId}/{nttId}", // 게시물 상세조회
            "/boardFileAtch/{bbsId}", // 게시판 파일 첨부가능 여부 조회
            "/schedule/daily", // 일별 일정 조회
            "/schedule/week", // 주간 일정 조회
            "/schedule/{schdulId}", // 일정 상세조회
            "/image", // 갤러리 이미지보기
    };

    // 인증 예외 List
    private String[] AUTH_WHITELIST = {
            "/",
            "/error", // 에러 페이지
            "/login/**",
            "/auth/login-jwt", // JWT 로그인
            "/auth/logout", // 로그아웃
            "/auth/find-user-id", // 아이디 찾기 (본인인증 DI)
            "/auth/account-recovery/**", // 아이디 찾기 (recoveryToken)
            "/auth/password-reset/**", // 비밀번호 재설정(본인인증)
            "/auth/oauth/**", // OAuth 로그인 URL 조회 (네이버, 카카오 등)
            "/OAuth/**", // OAuth 콜백 처리 (네이버, 카카오 등)
            "/file", // 파일 다운로드
            "/etc/**", // 사용자단의 회원약관,회원가입,사용자아이디 중복여부체크 URL허용

            /* 정적 리소스 */
            "/css/**",
            "/js/**",
            "/images/**",
            "/static/**",
            "/favicon.ico",
            "/index.html",

            /* swagger */
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**", // Swagger UI 정적 리소스

            /* OPTIONS 요청 허용 (CORS preflight) */
            "/api/**",

    };

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // properties에서 프론트엔드 URL 읽기 (쉼표로 구분된 여러 값 지원)
        String allowOriginStr = propertiesService.getString("Globals.Allow.Origin", "http://localhost:3000");
        
        // CORS 허용 Origin 목록 구성
        java.util.List<String> allowedOrigins = new java.util.ArrayList<>();
        
        // properties에서 읽은 값들을 쉼표로 분리하여 추가
        if (allowOriginStr != null && !allowOriginStr.isEmpty()) {
            String[] origins = allowOriginStr.split(",");
            for (String origin : origins) {
                String trimmedOrigin = origin.trim();
                if (!trimmedOrigin.isEmpty() && !allowedOrigins.contains(trimmedOrigin)) {
                    allowedOrigins.add(trimmedOrigin);
                }
            }
        }
        
        // 기본값이 없을 경우를 대비한 기본 URL 추가
        if (allowedOrigins.isEmpty()) {
            allowedOrigins.add("http://localhost:3000");
        }

        boolean devLikeProfile = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(p -> p.equalsIgnoreCase("dev") || p.equalsIgnoreCase("local"));
        /*
         * next dev --hostname 0.0.0.0 후 http://192.168.x.x:3000 으로 접속하면 Origin이 LAN IP가 된다.
         * Globals.Allow.Origin 에만 localhost 가 있으면 CORS 실패 → 브라우저는 fetch 를 "Failed to fetch" 로만 표시.
         */
        if (devLikeProfile) {
            Set<String> patterns = new LinkedHashSet<>();
            patterns.add("http://localhost:*");
            patterns.add("http://127.0.0.1:*");
            patterns.add("http://192.168.*:*");
            patterns.add("http://10.*:*");
            for (String origin : allowedOrigins) {
                if (origin == null || origin.isEmpty()) {
                    continue;
                }
                String o = origin.trim().replaceAll("/$", "");
                if (o.startsWith("http://") || o.startsWith("https://")) {
                    patterns.add(o.endsWith("*") ? o : o + "*");
                }
            }
            configuration.setAllowedOriginPatterns(new ArrayList<>(patterns));
        } else {
            configuration.setAllowedOrigins(allowedOrigins);
        }
        configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Bean
    public HTMLTagFilter htmlTagFilter() {
        return new HTMLTagFilter();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            ResultVO resultVO = new ResultVO();
            resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
            resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(resultVO);

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(jsonInString);
        };
    }

    // 멀티파트 필터 빈
    @Bean
    public MultipartFilter multipartFilter() {
        return new MultipartFilter();
    }

    // 서블릿 컨테이너에 멀티파트 구성을 제공하기 위한 설정
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize(DataSize.ofMegabytes(100L));
        factory.setMaxFileSize(DataSize.ofMegabytes(100L));
        return factory.createMultipartConfig();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    //authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    authorize.requestMatchers(AUTH_WHITELIST).permitAll();
                    //authorize.requestMatchers(HttpMethod.GET, AUTH_GET_WHITELIST).permitAll();
                    //authorize.requestMatchers("/admin/**").hasRole("USER");
                    authorize.requestMatchers(HttpMethod.GET, "/api/v1/files/download").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/api/v1/files/view").permitAll();
                    authorize.requestMatchers("/api/**").authenticated();
                    authorize.anyRequest().authenticated();

                    /*authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll(); // CORS preflight 요청 허용
                    authorize.requestMatchers(AUTH_WHITELIST).permitAll();
                    authorize.requestMatchers(HttpMethod.GET, AUTH_GET_WHITELIST).permitAll();
                    authorize.requestMatchers("/admin/**").hasRole("ADMIN");
                    authorize.requestMatchers("/admin/**").hasAnyRole("ADMIN","USER");
                    authorize.requestMatchers("/api/**").authenticated();
                    authorize.anyRequest().authenticated();*/
                })
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(securityContext ->
                        securityContext.securityContextRepository(new NullSecurityContextRepository()))
                .requestCache(requestCache -> requestCache.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(characterEncodingFilter(), ChannelProcessingFilter.class)
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(multipartFilter(), CsrfFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .build();
    }

}