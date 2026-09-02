package arami.common.auth.web;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import arami.common.auth.service.MemberLoginService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.jwt.EgovJwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.property.EgovPropertyService;

/**
 * JWT 기반 로그인을 처리하는 컨트롤러 클래스
 */
@Slf4j
@RestController
@Tag(name = "EgovLoginApiController", description = "로그인 관련")
public class EgovLoginApiController {

	@Resource(name = "memberLoginService")
	private MemberLoginService memberLoginService;

	@Resource(name = "egovMessageSource")
	EgovMessageSource egovMessageSource;

	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	@Resource(name = "leaveaTrace")
	LeaveaTrace leaveaTrace;

	@Autowired
	private EgovJwtTokenUtil jwtTokenUtil;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

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
		HashMap<String, Object> resultMap = new HashMap<>();

		LoginVO loginResultVO = memberLoginService.actionLogin(loginVO);

		if (loginResultVO != null && loginResultVO.getId() != null && !loginResultVO.getId().equals("")) {
			loginResultVO.setPassword("secret!!!");
			log.debug("===>>> loginResultVO.getUserSe() = " + loginResultVO.getUserSe());
			log.debug("===>>> loginResultVO.getId() = " + loginResultVO.getId());
			log.debug("===>>> loginResultVO.getGroupNm() = " + loginResultVO.getGroupNm());

			String jwtToken = jwtTokenUtil.generateToken(loginResultVO);

			String username = jwtTokenUtil.getUserSeFromToken(jwtToken);
			log.debug("Dec jwtToken username = " + username);
			String groupnm = jwtTokenUtil.getInfoFromToken("groupNm", jwtToken);
			log.debug("Dec jwtToken groupnm = " + groupnm);

			loginResultVO.setRole(loginResultVO.getGroupNm());

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					loginResultVO,
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
}
