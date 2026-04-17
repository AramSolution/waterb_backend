package arami.common.adminWeb.member.web;

import arami.common.CommonService;
import arami.common.CryptoUtil;
import arami.common.adminWeb.member.service.AdminUserMemberManageService;
import arami.common.adminWeb.member.service.dto.request.MemberListRequest;
import arami.common.adminWeb.member.service.dto.request.MemberInsertRequest;
import arami.common.adminWeb.member.service.dto.request.MemberUpdateRequest;
import arami.common.adminWeb.member.service.dto.request.MemberDetailRequest;
import arami.common.adminWeb.member.service.dto.request.MemberDeleteRequest;
import arami.common.adminWeb.member.service.dto.response.MemberListResponse;
import arami.common.adminWeb.member.service.dto.response.MemberDTO;
import arami.common.adminWeb.member.service.dto.response.AdminMemberResultResponse;
import arami.common.adminWeb.member.service.dto.response.MemberDetailResponse;
import arami.common.adminWeb.member.service.dto.response.MemberExcelListResponse;
import arami.common.adminWeb.member.service.dto.response.AuthListResponse;
import arami.common.userWeb.member.service.MemberUsrService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;
import egovframework.let.utl.sim.service.EgovFileScrty;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Slf4j
@Tag(name = "관리자회원", description = "관리자웹 - 관리자회원 관리 API")
@RestController
@RequestMapping("/api/admin/member")
public class AdminUserMemberManageController extends CommonService {

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Resource
	private MemberUsrService memberUsrService;

    /* 사용이유 : Egov 자동코드 생성 */
	@Resource(name = "egovUsrCnfrmIdGnrService")
	private EgovIdGnrService idgenService;
    
	@Resource
	private AdminUserMemberManageService adminUserMemberManageService;
	
	/** 암호화 유틸리티 */
	@Resource
	private CryptoUtil cryptoUtil;
	
	// =============================================
	// (AJAX / DATATABLE ) : 관리자회원 리스트 조회
	// =============================================
	@Operation(summary = "관리자회원 목록 조회", description = "페이징/검색 조건으로 관리자회원 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@ResponseBody
    @PostMapping(value = "/selectAdminUserMemberList.Ajax", produces="application/json;charset=UTF-8")
    public MemberListResponse selectAdminUserMemberList(
    		@RequestBody MemberListRequest request) throws Exception {

		// 페이징 기본값 설정
		request.setDefaultPaging();

		MemberListResponse response = new MemberListResponse();

		try {
			int totalCount = adminUserMemberManageService.selectAdminUserMemberListCount(request);
			List<MemberDTO> data = adminUserMemberManageService.selectAdminUserMemberList(request);
			
			response.setData(data);
			response.setRecordsFiltered(totalCount);
			response.setRecordsTotal(totalCount);
			response.setResult("00");
			
		} catch (Exception e) {
			log.error("Error in selectAdminUserMemberList: " + e.getMessage(), e);
			response.setResult("01");
			if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				System.out.println("Connection Exception occurred");
				e.printStackTrace();
			}
		}

		return response;
	}

    // =============================================
	// (AJAX / DATATABLE ) : 관리자회원 엑셀 리스트 조회
	// =============================================
	@Operation(summary = "관리자회원 엑셀 목록 조회", description = "관리자회원 목록을 엑셀 다운로드용으로 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@ResponseBody
    @PostMapping(value = "/selectAdminUserMemberExcelList.Ajax", produces="application/json;charset=UTF-8")
    public MemberExcelListResponse selectAdminUserMemberExcelList(HttpServletRequest request, ModelMap model) throws Exception {

		/** (초기설정) */
		this.setCommon(request, model);
		MemberExcelListResponse response = new MemberExcelListResponse();

		try {
			// 관리자회원 엑셀 리스트 조회
			List<Object> data = adminUserMemberManageService.selectAdminUserMemberExcelList(model);
			response.setData(data);
			response.setResult("00");
		} catch (Exception e) {
			response.setResult("01");
			if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				System.out.println("Connection Exception occurred");
				e.printStackTrace();
			}
		}

		return response;
	}

	@Operation(summary = "관리자회원 등록 화면", description = "등록 화면용 권한 목록을 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping(value = "/insertAdminUserMemberManage.adm")
		public AuthListResponse insertAdminUserMemberManage(HttpServletRequest request, ModelMap model) throws Exception {
			this.setCommon(request, model);
			
			AuthListResponse response = new AuthListResponse();
			response.setAuthList(adminUserMemberManageService.selectAuthList(model));

			return response;
		}

    // ========================================
	// (AJAX|INSERT) 관리자회원 등록 AJAX
	// ========================================
	@Operation(summary = "관리자회원 등록", description = "새 관리자회원을 등록합니다. 아이디 중복 시 50 코드를 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "등록 성공(00) / 아이디 중복(50) / 실패(01)"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@ResponseBody
    @PostMapping(value = "/insertAdminUserMember.Ajax", produces="application/json;charset=UTF-8")
	public AdminMemberResultResponse insertAdminUserMember(
			@RequestBody MemberInsertRequest request) throws Exception {

		AdminMemberResultResponse response = new AdminMemberResultResponse();

		try {
			// ====================== 아이디 중복 체크 실행 ======================
			ModelMap checkModel = new ModelMap();
			checkModel.put("userId", request.getUserId());
			String checkYn = memberUsrService.checkMemberId(checkModel);	// Y : 가능 N : 불가

			if( "Y".equals(checkYn) ) {
				/** [1] 코드채번 + SHA256 */
				String esntlId = idgenService.getNextStringId();
				// EgovFramework 자동 코드 생성
				String newPw = EgovFileScrty.encryptPassword(request.getPassword(), request.getUserId());

				// [2] DTO 설정
				request.setEsntlId(esntlId);
				request.setNewPw(newPw);
				request.setUserSe("USR");
				
				// [2-1] 개인정보 암호화 (LEA 방식) - CryptoFields에 정의된 필드 자동 암호화
				//cryptoUtil.encryptPersonalInfo(request);

				// 관리자 일반정보 등록
				int result1 = adminUserMemberManageService.insertAdminMemberAjax(request);

				response.setResult("00");
				response.setMessage(egovMessageSource.getMessage("success.common.insert"));
				if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
					//System.out.println("[system.out] 등록 처리 결과 : " + result);
				}

			}else{
				response.setResult("50");
				response.setMessage("중복되는 아이디가 있습니다. 다른 아이디를 사용하여 주십시요.");
			}

		} catch (Exception e) {
			log.error("Error in insertAdminUserMember: " + e.getMessage(), e);
			response.setResult("01");
			response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
			if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				System.out.println("Connection Exception occurred");
				e.printStackTrace();
			}
		}

		return response;
	}

	// ======================================
	// (화면출력|JSTL) 관리자회원관리 - 수정화면
	// ======================================
	@Operation(summary = "관리자회원 수정 화면", description = "수정 화면용 권한 목록을 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping(value = "/updateAdminUserMemberManage.adm")
		public AuthListResponse updateAdminUserMemberManage(HttpServletRequest request, ModelMap model) throws Exception {
			this.setCommon(request, model);
			
			AuthListResponse response = new AuthListResponse();
			response.setAuthList(adminUserMemberManageService.selectAuthList(model));

			return response;
		}

	@Operation(summary = "관리자회원 상세 조회", description = "관리자회원 한 건의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping(value = "/selectAdminUserMemberDetail.Ajax")
	public MemberDetailResponse selectAdminUserMemberDetail(
			@RequestBody MemberDetailRequest request) throws Exception {
		
		MemberDetailResponse response = new MemberDetailResponse();
		MemberDTO adminInfo = adminUserMemberManageService.selectAdminUserMemberDetail(request);
		response.setAdminInfo(adminInfo);

		return response;
	}

	// ========================================
	// (AJAX|INSERT) 관리자회원 수정 AJAX
	// ========================================
	@Operation(summary = "관리자회원 수정", description = "관리자회원 정보를 수정합니다. 아이디 중복 시 50 코드를 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "수정 성공(00) / 아이디 중복(50) / 실패(01)"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@ResponseBody
    @PostMapping(value = "/updateAdminUserMember.Ajax", produces="application/json;charset=UTF-8")
	public AdminMemberResultResponse updateAdminUserMember(
			@RequestBody MemberUpdateRequest request) throws Exception {

		AdminMemberResultResponse response = new AdminMemberResultResponse();

		try {
			// ====================== 아이디 중복 체크 실행 ======================
			ModelMap checkModel = new ModelMap();
			checkModel.put("userId", request.getUserId());
			checkModel.put("esntlId", request.getEsntlId());
			String checkYn = memberUsrService.checkMemberId2(checkModel);	// Y : 가능 N : 불가

			if( "Y".equals(checkYn) ) {
				String newPw = "";
				
				if( request.getPassword() != null && !"".equals(request.getPassword()) ) {
					// EgovFramework 자동 코드 생성
					newPw = EgovFileScrty.encryptPassword(request.getPassword(), request.getUserId());
					request.setNewPw(newPw);
				}
				
				// [1-1] 개인정보 암호화 (LEA 방식) - CryptoFields에 정의된 필드 자동 암호화
				//cryptoUtil.encryptPersonalInfo(request);

				// 관리자 일반정보 수정
				int result1 = adminUserMemberManageService.updateAdminUserMember(request);

				response.setResult("00");
				response.setMessage(egovMessageSource.getMessage("success.common.update"));
				if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
					//System.out.println("[system.out] 등록 처리 결과 : " + result);
				}
			}else{
				response.setResult("50");
				response.setMessage("중복되는 아이디가 있습니다. 다른 아이디를 사용하여 주십시요.");
			}

		} catch (Exception e) {
			log.error("Error in updateAdminUserMember: " + e.getMessage(), e);
			response.setResult("01");
			response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
			if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				System.out.println("Connection Exception occurred");
				e.printStackTrace();
			}
		}

		return response;
	}

	// ========================================
	// (AJAX|INSERT) 관리자회원 탈퇴 AJAX
	// ========================================
	@Operation(summary = "관리자회원 탈퇴(삭제)", description = "관리자회원을 탈퇴(삭제) 처리합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "탈퇴 성공(00) / 실패(01)"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@ResponseBody
    @PostMapping(value = "/deleteAdminUserMember.Ajax", produces="application/json;charset=UTF-8")
	public AdminMemberResultResponse deleteAdminUserMember(
			@RequestBody MemberDeleteRequest request) throws Exception {

		AdminMemberResultResponse response = new AdminMemberResultResponse();

		try {
			// 관리자 일반정보 탈퇴
			int result1 = adminUserMemberManageService.deleteUserMemberAjax(request);

			response.setResult("00");
			response.setMessage("정상적으로 탈퇴되었습니다.");

		} catch (Exception e) {
			log.error("Error in deleteAdminUserMember: " + e.getMessage(), e);
			response.setResult("01");
			response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
			if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				System.out.println("Connection Exception occurred");
				e.printStackTrace();
			}
		}

		return response;
	}
}