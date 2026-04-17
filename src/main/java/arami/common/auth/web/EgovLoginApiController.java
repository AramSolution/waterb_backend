package arami.common.auth.web;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import arami.common.CryptoUtil;
import arami.common.auth.service.MemberLoginService;
import arami.common.auth.service.dto.OAuthJoinCheckResult;
import arami.shared.armuser.dto.request.ArmuserInsertRequest;
import arami.shared.armuser.dto.response.ArmuserResultResponse;
import arami.shared.armuser.service.ArmuserService;
import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.service.EgovProperties;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.ext.oauth.service.OAuthLogin;
import egovframework.com.ext.oauth.service.OAuthUniversalUser;
import egovframework.com.ext.oauth.service.OAuthVO;
import egovframework.com.jwt.EgovJwtTokenUtil;
import egovframework.com.jwt.OAuthLinkTokenClaims;
import egovframework.com.jwt.OAuthLinkTokenUtil;
import egovframework.com.jwt.OAuthMypageLinkTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 기반 로그인을 처리하는 컨트롤러 클래스
 * @author 공통서비스 개발팀 박지욱
 * @since 2009.03.06
 * @version 2.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일      수정자      수정내용
 *  -------            --------        ---------------------------
 *  2009.03.06  박지욱     최초 생성
 *  2011.08.31  JJY            경량환경 템플릿 커스터마이징버전 생성
 *  2025.11.10             JWT 기반 로그인으로 전환, 세션 기반 로그인 제거
 *
 *  </pre>
 */
@Slf4j
@RestController
@Tag(name="EgovLoginApiController",description = "로그인 관련")
public class EgovLoginApiController {

	/** EgovLoginService */
	@Resource(name = "memberLoginService")
	private MemberLoginService memberLoginService;

	/** EgovMessageSource */
	@Resource(name = "egovMessageSource")
	EgovMessageSource egovMessageSource;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** TRACE */
	@Resource(name = "leaveaTrace")
	LeaveaTrace leaveaTrace;

	/** JWT */
	@Autowired
    private EgovJwtTokenUtil jwtTokenUtil;

	@Autowired
	private OAuthLinkTokenUtil oAuthLinkTokenUtil;

	@Autowired
	private OAuthMypageLinkTokenUtil oAuthMypageLinkTokenUtil;

	/** ApplicationEventPublisher */
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/** 암호화 유틸리티 */
	@Resource
	private CryptoUtil cryptoUtil;

	/** Naver OAuth VO */
	@Resource(name = "naverAuthVO")
	private OAuthVO naverAuthVO;

	/** Kakao OAuth VO */
	@Resource(name = "kakaoAuthVO")
	private OAuthVO kakaoAuthVO;

	@Resource(name = "armuserService")
	private ArmuserService armuserService;

	/** OAuth 콜백 리다이렉트용 프론트엔드 도메인 */
	@Value("${Globals.frontendDomain:http://localhost:3000}")
	private String frontendDomain;

	/** state(학생/학부모/학원/멘토) → USER_SE */
	private static final java.util.Map<String, String> STATE_TO_USER_SE = new java.util.HashMap<>();
	static {
		STATE_TO_USER_SE.put("student", "SNR");
		STATE_TO_USER_SE.put("parent", "PNR");
		STATE_TO_USER_SE.put("academy", "ANR");
		STATE_TO_USER_SE.put("mentor", "MNR");
	}

	private static final String OAUTH_MODE_MYPAGE_LINK = "mypage_link";

	private static String stripBearerToken(String authorization) {
		if (authorization == null) {
			return null;
		}
		String t = authorization.trim();
		if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
			return t.substring(7).trim();
		}
		return t;
	}

	/**
	 * MY PAGE SNS 연결 완료/실패 후 프론트 경로 (로그인 OAuth와 분리).
	 * 학생·학부모·학원·멘토 모두 {@code /userWeb/mypagePr} + MypageSection에서 나의정보·SNS 연동 UI를 쓰므로 동일 경로로 복귀한다.
	 */
	private String mypageLinkRedirectUrl(String errorCode) {
		String base = frontendDomain + "/userWeb/mypagePr";
		if (errorCode == null || errorCode.isEmpty()) {
			return base + "?oauth_link=ok";
		}
		return base + "?oauth_link_error=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8);
	}

	/** 포털 루트(/userWeb) OAuth 로그인 오류 쿼리 리다이렉트 */
	private String portalOAuthErrorRedirectUrl(String oauthError, String redirectState) {
		String q = "oauth_error=" + URLEncoder.encode(oauthError, StandardCharsets.UTF_8);
		if (redirectState != null && !redirectState.isEmpty()) {
			q += "&state=" + URLEncoder.encode(redirectState, StandardCharsets.UTF_8);
		}
		return frontendDomain + "/userWeb?" + q;
	}

	/**
	 * JWT 기반 로그인을 처리한다
	 * @param loginVO - 아이디, 비밀번호가 담긴 LoginVO
	 * @param request - HttpServletRequest
	 * @return HashMap - 로그인 결과(JWT 토큰, 사용자 정보)
	 * @exception Exception
	 */
	@Operation(
			summary = "JWT 로그인",
			description = "JWT 로그인 처리",
			tags = {"EgovLoginApiController"}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "로그인 성공"),
			@ApiResponse(responseCode = "300", description = "로그인 실패")
	})
	@PostMapping(value = "/auth/login-jwt")
	public HashMap<String, Object> actionLoginJWT(@RequestBody LoginVO loginVO, HttpServletRequest request) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		// 1. JWT 로그인 처리
		LoginVO loginResultVO = memberLoginService.actionLogin(loginVO);

		if (loginResultVO != null && loginResultVO.getId() != null && !loginResultVO.getId().equals("")) {
			/*if(loginResultVO.getGroupNm().equals("ROLE_ADMIN")) {//로그인 결과에서 스프링시큐리티용 그룹명값에 따른 권한부여
				loginResultVO.setUserSe("ADM");
	        }*/
			loginResultVO.setPassword("secret!!!");
			log.debug("===>>> loginResultVO.getUserSe() = "+loginResultVO.getUserSe());
			log.debug("===>>> loginResultVO.getId() = "+loginResultVO.getId());
			log.debug("===>>> loginResultVO.getPassword() = "+loginResultVO.getPassword());
			log.debug("===>>> loginResultVO.getGroupNm() = "+loginResultVO.getGroupNm());//로그인 결과에서 스프링시큐리티용 그룹명값 출력

			String jwtToken = jwtTokenUtil.generateToken(loginResultVO);

			String username = jwtTokenUtil.getUserSeFromToken(jwtToken);
	    	log.debug("Dec jwtToken username = "+username);
	    	String groupnm = jwtTokenUtil.getInfoFromToken("groupNm", jwtToken);
	    	log.debug("Dec jwtToken groupnm = "+groupnm);//생성한 토큰에서 스프링시큐리티용 그룹명값 출력
	    	// JWT 토큰을 클라이언트에 반환
	    	// 클라이언트는 이후 모든 요청의 Authorization 헤더에 JWT 토큰을 포함하여 전송
	    	// JwtAuthenticationFilter가 토큰을 검증하고 SecurityContext에 인증 정보를 설정

			// GroupNm을 ROLE에 세팅
			loginResultVO.setRole(loginResultVO.getGroupNm());

			// Spring Security 인증 객체 생성 및 이벤트 발행 (로그 기록을 위해)
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					loginResultVO,  // LoginVO 객체 전체를 principal로 설정
					null,
					java.util.Collections.singletonList(new SimpleGrantedAuthority(loginResultVO.getGroupNm()))
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			eventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));

			resultMap.put("resultVO", loginResultVO);
			resultMap.put("accessToken", jwtToken);
			resultMap.put("resultCode", "200");
			resultMap.put("resultMessage", "성공 !!!");

		} else {
			resultMap.put("resultVO", loginResultVO);
			resultMap.put("resultCode", "300");
			resultMap.put("resultMessage", egovMessageSource.getMessage("fail.common.login"));
		}

		return resultMap;
	}

	/**
	 * 로그아웃한다. SecurityContext를 초기화한다.
	 * @return resultVO - 로그아웃 결과
	 * @exception Exception
	 */
	@Operation(
			summary = "로그아웃",
			description = "JWT 로그아웃 처리 (SecurityContext 초기화)",
			security = {@SecurityRequirement(name = "Authorization")},
			tags = {"EgovLoginApiController"}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "로그아웃 성공"),
	})
	@GetMapping(value = "/auth/logout")
	public ResultVO actionLogoutJSON(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ResultVO resultVO = new ResultVO();

		new SecurityContextLogoutHandler().logout(request, response, null);

		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
		resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());

		return resultVO;
	}

	/**
	 * OAuth 로그인 URL을 반환한다
	 * @param oauthService - naver 또는 kakao
	 * @param request - HttpServletRequest
	 * @return HashMap - OAuth 로그인 URL
	 * @exception Exception
	 */
	@Operation(
			summary = "OAuth 로그인 URL 조회",
			description = "네이버/카카오 OAuth 로그인 URL 반환",
			tags = {"EgovLoginApiController"}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "성공"),
			@ApiResponse(responseCode = "400", description = "잘못된 OAuth 서비스명")
	})
	@GetMapping(value = "/auth/oauth/{oauthService}/url")
	public HashMap<String, Object> getOAuthUrl(
			@PathVariable String oauthService,
			@RequestParam(required = false) String state,
			@RequestParam(required = false) String mode,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			HttpServletRequest request) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			OAuthVO oauthVO = null;
			if ("naver".equalsIgnoreCase(oauthService)) {
				oauthVO = naverAuthVO;
			} else if ("kakao".equalsIgnoreCase(oauthService)) {
				oauthVO = kakaoAuthVO;
			} else {
				resultMap.put("resultCode", "400");
				resultMap.put("resultMessage", "지원하지 않는 OAuth 서비스입니다.");
				return resultMap;
			}

			String oauthState = state;
			if (OAUTH_MODE_MYPAGE_LINK.equalsIgnoreCase(StringUtils.trimWhitespace(mode == null ? "" : mode))) {
				String stateKey = StringUtils.trimWhitespace(state == null ? "" : state);
				if (!STATE_TO_USER_SE.containsKey(stateKey)) {
					resultMap.put("resultCode", "400");
					resultMap.put("resultMessage", "MY PAGE 연결에는 state(회원 유형)가 필요합니다.");
					return resultMap;
				}
				String accessToken = stripBearerToken(authorization);
				if (!StringUtils.hasText(accessToken)) {
					resultMap.put("resultCode", "401");
					resultMap.put("resultMessage", "로그인이 필요합니다.");
					return resultMap;
				}
				LoginVO tokenUser;
				try {
					tokenUser = jwtTokenUtil.getLoginVOFromToken(accessToken);
				} catch (Exception e) {
					log.debug("MY PAGE OAuth URL: invalid JWT", e);
					resultMap.put("resultCode", "401");
					resultMap.put("resultMessage", "유효하지 않은 토큰입니다.");
					return resultMap;
				}
				String expectedSe = STATE_TO_USER_SE.get(stateKey);
				if (tokenUser.getUserSe() == null || !expectedSe.equals(tokenUser.getUserSe())) {
					resultMap.put("resultCode", "403");
					resultMap.put("resultMessage", "회원 유형이 일치하지 않습니다.");
					return resultMap;
				}
				if (!StringUtils.hasText(tokenUser.getUniqId())) {
					resultMap.put("resultCode", "400");
					resultMap.put("resultMessage", "사용자 고유 ID가 없습니다.");
					return resultMap;
				}
				// state에는 RFC 3986 안전 문자인 base64url(jwt)만 사용 (Tomcat request-target 400 방지)
				oauthState = oAuthMypageLinkTokenUtil.createMypageLinkToken(tokenUser.getUniqId(), tokenUser.getUserSe(), stateKey);
			}

			OAuthLogin oauthLogin = new OAuthLogin(oauthVO, oauthState);
			String oauthUrl = oauthLogin.getOAuthURL();

			resultMap.put("resultCode", "200");
			resultMap.put("resultMessage", "성공");
			resultMap.put("oauthUrl", oauthUrl);

		} catch (Exception e) {
			log.error("OAuth URL 생성 오류", e);
			resultMap.put("resultCode", "500");
			resultMap.put("resultMessage", "OAuth URL 생성 중 오류가 발생했습니다.");
		}

		return resultMap;
	}

	/**
	 * OAuth 콜백을 처리한다. 가입 여부에 따라 프론트엔드로 302 리다이렉트한다.
	 * - 가입된 회원: JWT 발급 후 /userWeb/oauth/callback?access_token=...&state=... 로 리다이렉트
	 * - 미가입: /userWeb/joinAc?type=... 로 리다이렉트
	 */
	@Operation(
			summary = "OAuth 콜백 처리",
			description = "네이버/카카오 OAuth 콜백 처리 후 프론트엔드로 리다이렉트 (가입 시 로그인, 미가입 시 회원가입)",
			tags = {"EgovLoginApiController"}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "302", description = "프론트엔드로 리다이렉트"),
	})
	@GetMapping(value = "/OAuth/{oauthService}/callback")
	public ResponseEntity<Void> oauthCallback(
			@PathVariable String oauthService,
			@RequestParam(required = false) String code,
			@RequestParam(required = false) String state,
			@RequestParam(required = false) String error,
			HttpServletRequest request) throws Exception {

		String redirectState = "student";
		OAuthMypageLinkTokenUtil.MypageLinkClaims mypageClaims = null;
		boolean mypageLinkFlow = false;
		// MY PAGE 연결: state 자체가 단기 JWT (base64url) 이므로 우선 파싱을 시도
		if (state != null && !state.isBlank()) {
			try {
				mypageClaims = oAuthMypageLinkTokenUtil.parseMypageLinkToken(state.trim());
				String stateKey = mypageClaims.getStateKey();
				if (stateKey != null && STATE_TO_USER_SE.containsKey(stateKey)) {
					mypageLinkFlow = true;
					redirectState = stateKey;
				} else {
					mypageClaims = null;
				}
			} catch (IllegalArgumentException ignored) {
				// not a mypage link token
			}
		}
		if (!mypageLinkFlow && state != null && STATE_TO_USER_SE.containsKey(state)) {
			redirectState = state;
		}
		String userSe = STATE_TO_USER_SE.get(redirectState);

		if (error != null) {
			if (mypageLinkFlow) {
				return ResponseEntity.status(HttpStatus.FOUND)
						.location(URI.create(mypageLinkRedirectUrl("cancelled")))
						.build();
			}
			return ResponseEntity.status(HttpStatus.FOUND)
					.location(URI.create(portalOAuthErrorRedirectUrl("cancelled", redirectState)))
					.build();
		}

		if (code == null || code.isEmpty()) {
			if (mypageLinkFlow) {
				return ResponseEntity.status(HttpStatus.FOUND)
						.location(URI.create(mypageLinkRedirectUrl("no_code")))
						.build();
			}
			return ResponseEntity.status(HttpStatus.FOUND)
					.location(URI.create(portalOAuthErrorRedirectUrl("no_code", redirectState)))
					.build();
		}

		OAuthVO oauthVO = null;
		if ("naver".equalsIgnoreCase(oauthService)) {
			oauthVO = naverAuthVO;
		} else if ("kakao".equalsIgnoreCase(oauthService)) {
			oauthVO = kakaoAuthVO;
		} else {
			return ResponseEntity.status(HttpStatus.FOUND)
					.location(URI.create(portalOAuthErrorRedirectUrl("invalid_service", redirectState)))
					.build();
		}

		// OAuth 토큰 교환 진단용 (client_secret 값은 로그에 남기지 않음)
		String dbgClientId = oauthVO.getClientId();
		String dbgClientSecret = oauthVO.getClientSecret();
		log.info(
				"[OAuth callback] service={} redirectState={} codeLen={} clientIdNull={} clientId={} clientSecretNull={} clientSecretBlank={} clientSecretLen={} redirectUri={}",
				oauthService,
				redirectState,
				code.length(),
				dbgClientId == null,
				dbgClientId,
				dbgClientSecret == null,
				dbgClientSecret != null && dbgClientSecret.isBlank(),
				dbgClientSecret != null ? dbgClientSecret.length() : 0,
				oauthVO.getRedirectUrl());

		try {
			OAuthLogin oauthLogin = new OAuthLogin(oauthVO);
			OAuthUniversalUser oauthUser = oauthLogin.getUserProfile(code);

			// OAuth 로그인 콜백 시점의 파싱 결과(이메일/전화/프로필 등) 디버깅 로그
			// 민감정보가 포함될 수 있으므로 Globals.debug=true 일 때만 남깁니다.
			if ("true".equalsIgnoreCase(EgovProperties.getProperty("Globals.debug"))) {
				log.info(
						"[EgovLoginApiController/OAuth callback] oauthUser uid={} serviceName={} userId={} userName={} email={} phoneNumber={} profileImage={}",
						oauthUser.getUid(),
						oauthUser.getServiceName(),
						oauthUser.getUserId(),
						oauthUser.getUserName(),
						oauthUser.getEmail(),
						oauthUser.getPhoneNumber(),
						oauthUser.getProfileImage());
			}

			// MY PAGE: 로그인된 계정에 SNS id만 저장 (이메일 매칭·가입·link_token 플로우 없음)
			if (mypageLinkFlow) {
				try {
					if (mypageClaims == null || !StringUtils.hasText(mypageClaims.getEsntlId()) || !StringUtils.hasText(mypageClaims.getUserSe())) {
						return ResponseEntity.status(HttpStatus.FOUND)
								.location(URI.create(mypageLinkRedirectUrl("invalid_link_token")))
								.build();
					}
					LoginVO row = memberLoginService.actionLoginByEsntlId(mypageClaims.getEsntlId());
					if (row == null || row.getUserSe() == null || !row.getUserSe().equals(userSe)
							|| row.getUniqId() == null || !row.getUniqId().equals(mypageClaims.getEsntlId())) {
						return ResponseEntity.status(HttpStatus.FOUND)
								.location(URI.create(mypageLinkRedirectUrl("user_mismatch")))
								.build();
					}
					String oauthAuthId = oauthUser.getUserId() != null ? oauthUser.getUserId().trim() : "";
					if (oauthAuthId.isEmpty()) {
						return ResponseEntity.status(HttpStatus.FOUND)
								.location(URI.create(mypageLinkRedirectUrl("no_oauth_id")))
								.build();
					}
					if ("naver".equalsIgnoreCase(oauthService)) {
						String other = memberLoginService.selectEsntlIdByNaverAuthIdExcluding(oauthAuthId, mypageClaims.getEsntlId());
						if (StringUtils.hasText(other)) {
							return ResponseEntity.status(HttpStatus.FOUND)
									.location(URI.create(mypageLinkRedirectUrl("already_linked")))
									.build();
						}
						memberLoginService.updateOAuthId(mypageClaims.getEsntlId(), "01", oauthAuthId, null);
					} else if ("kakao".equalsIgnoreCase(oauthService)) {
						String other = memberLoginService.selectEsntlIdByKakaoAuthIdExcluding(oauthAuthId, mypageClaims.getEsntlId());
						if (StringUtils.hasText(other)) {
							return ResponseEntity.status(HttpStatus.FOUND)
									.location(URI.create(mypageLinkRedirectUrl("already_linked")))
									.build();
						}
						memberLoginService.updateOAuthId(mypageClaims.getEsntlId(), "02", null, oauthAuthId);
					} else {
						return ResponseEntity.status(HttpStatus.FOUND)
								.location(URI.create(mypageLinkRedirectUrl("invalid_service")))
								.build();
					}
					return ResponseEntity.status(HttpStatus.FOUND)
							.location(URI.create(mypageLinkRedirectUrl(null)))
							.build();
				} catch (IllegalArgumentException e) {
					log.warn("MY PAGE OAuth link: {}", e.getMessage());
					return ResponseEntity.status(HttpStatus.FOUND)
							.location(URI.create(mypageLinkRedirectUrl("invalid_link_token")))
							.build();
				} catch (Exception e) {
					log.error("MY PAGE OAuth link update failed", e);
					return ResponseEntity.status(HttpStatus.FOUND)
							.location(URI.create(mypageLinkRedirectUrl("update_failed")))
							.build();
				}
			}

			String email = (oauthUser.getEmail() != null) ? oauthUser.getEmail().trim() : "";
			if (email.isEmpty()) {
				return ResponseEntity.status(HttpStatus.FOUND)
						.location(URI.create(portalOAuthErrorRedirectUrl("no_email", redirectState)))
						.build();
			}

			OAuthJoinCheckResult join = memberLoginService.selectOAuthJoinCheck(email, userSe);

			// [신규] terms 없이 즉시 가입 → 바로 로그인(JWT 발급)
			if (join == null || join.getEsntlId() == null || join.getEsntlId().isEmpty()) {
				try {
					String existingUserSe = memberLoginService.selectUserSeByOauthUserId(email);
					if (existingUserSe != null && !existingUserSe.isEmpty() && !existingUserSe.equals(userSe)) {
						return ResponseEntity.status(HttpStatus.FOUND)
								.location(URI.create(portalOAuthErrorRedirectUrl("email_used_other_member_type", redirectState)))
								.build();
					}
				} catch (Exception e) {
					log.warn("OAuth callback: selectUserSeByOauthUserId", e);
				}

				ArmuserInsertRequest insert = new ArmuserInsertRequest();
				insert.setUserSe(userSe);
				insert.setUserId(email);
				String rawPw = UUID.randomUUID().toString().replace("-", "");
				insert.setPassword(rawPw);
				String userNm = (oauthUser.getUserName() != null && !oauthUser.getUserName().trim().isEmpty())
						? oauthUser.getUserName().trim()
						: "SNS회원";
				insert.setUserNm(userNm);
				insert.setEmailAdres(email);
				String phone = oauthUser.getPhoneNumber() != null ? oauthUser.getPhoneNumber().trim() : "";
				if (!phone.isEmpty()) {
					insert.setMbtlnum(phone);
					insert.setUsrTelno(phone);
				}
				// terms 생략(즉시 가입)이므로 동의값은 Y로 저장
				insert.setTerms1Yn("Y");
				insert.setTerms2Yn("Y");
				insert.setTerms3Yn("Y");
				insert.setTerms4Yn("Y");

				// 신규 즉시가입 시 가입일자(SBSCRB_DE)를 오늘 날짜로 저장 (YYYY-MM-DD)
				insert.setSbscrbDe(LocalDate.now().toString());

				String oauthAuthId = oauthUser.getUserId() != null ? oauthUser.getUserId().trim() : "";
				if ("naver".equalsIgnoreCase(oauthService)) {
					// ARMUSER.OAUTH_GB: 일반=00, 네이버=01, 카카오=02
					insert.setOauthGb("01");
					insert.setNaverAuthId(oauthAuthId);
				} else if ("kakao".equalsIgnoreCase(oauthService)) {
					// ARMUSER.OAUTH_GB: 일반=00, 네이버=01, 카카오=02
					insert.setOauthGb("02");
					insert.setKakaoAuthId(oauthAuthId);
				}

				ArmuserResultResponse r = armuserService.insertArmuser(insert);
				if (r != null) {
					String oauthErr = "signup_failed";
					if ("50".equals(r.getResult())) {
						try {
							String existingUserSe = memberLoginService.selectUserSeByOauthUserId(email);
							if (existingUserSe != null && !existingUserSe.isEmpty() && !existingUserSe.equals(userSe)) {
								oauthErr = "email_used_other_member_type";
							}
						} catch (Exception ex) {
							log.debug("OAuth callback: post-insert duplicate userSe check", ex);
						}
					}
					return ResponseEntity.status(HttpStatus.FOUND)
							.location(URI.create(portalOAuthErrorRedirectUrl(oauthErr, redirectState)))
							.build();
				}
				LoginVO loginVO = memberLoginService.actionLoginByOAuthEmail(email, userSe);
				if (loginVO != null && loginVO.getId() != null && !loginVO.getId().isEmpty()) {
					String jwtToken = jwtTokenUtil.generateToken(loginVO);
					String redirectUrl = frontendDomain + "/userWeb/oauth/callback?access_token=" + java.net.URLEncoder.encode(jwtToken, "UTF-8") + "&state=" + redirectState;
					return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
				}
				return ResponseEntity.status(HttpStatus.FOUND)
						.location(URI.create(portalOAuthErrorRedirectUrl("login_failed", redirectState)))
						.build();
			}

			// [기존] 연동 여부 확인 → 연동 안 되어 있으면 로그인 차단(링크 동의 화면으로)
			boolean linked = false;
			if ("naver".equalsIgnoreCase(oauthService)) {
				linked = join.getNaverAuthId() != null && !join.getNaverAuthId().trim().isEmpty();
			} else if ("kakao".equalsIgnoreCase(oauthService)) {
				linked = join.getKakaoAuthId() != null && !join.getKakaoAuthId().trim().isEmpty();
			}

			if (!linked) {
				OAuthLinkTokenClaims c = new OAuthLinkTokenClaims();
				c.setEmail(email);
				c.setUserSe(userSe);
				c.setOauthService(oauthService);
					// ARMUSER.OAUTH_GB: 일반=00, 네이버=01, 카카오=02
					c.setOauthGb("naver".equalsIgnoreCase(oauthService) ? "01" : "02");
				c.setOauthAuthId(oauthUser.getUserId());
				c.setState(redirectState);
				String linkToken = oAuthLinkTokenUtil.createLinkToken(c);

				String redirectUrl = frontendDomain + "/userWeb/oauth/link?link_token=" + java.net.URLEncoder.encode(linkToken, "UTF-8") + "&state=" + redirectState;
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
			}

			// [기존 + 연동됨] 바로 로그인(JWT 발급)
			LoginVO loginVO = memberLoginService.actionLoginByOAuthEmail(email, userSe);
			if (loginVO != null && loginVO.getId() != null && !loginVO.getId().isEmpty()) {
				loginVO.setPassword("secret!!!");
				String jwtToken = jwtTokenUtil.generateToken(loginVO);
				loginVO.setRole(loginVO.getGroupNm());
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						loginVO, null,
						java.util.Collections.singletonList(new SimpleGrantedAuthority(loginVO.getGroupNm() != null ? loginVO.getGroupNm() : "ROLE_USER"))
				);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				eventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));

				String redirectUrl = frontendDomain + "/userWeb/oauth/callback?access_token=" + java.net.URLEncoder.encode(jwtToken, "UTF-8") + "&state=" + redirectState;
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
			}

			return ResponseEntity.status(HttpStatus.FOUND)
					.location(URI.create(portalOAuthErrorRedirectUrl("login_failed", redirectState)))
					.build();

		} catch (Exception e) {
			log.error("OAuth 콜백 처리 오류", e);
			return ResponseEntity.status(HttpStatus.FOUND)
					.location(URI.create(portalOAuthErrorRedirectUrl("server", null)))
					.build();
		}
	}
}