package arami.common.adminWeb.code.web;

import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import arami.common.CommonService;
import arami.common.adminWeb.code.service.CmmCodeManageService;
import arami.common.adminWeb.code.service.dto.response.BuildingUseCodeTreeResponse;
import arami.common.adminWeb.code.service.dto.response.DetailCodeListByCategoryResponse;
import arami.common.adminWeb.code.service.dto.response.DetailCodeResponse;
import egovframework.com.cmm.service.EgovProperties;

/**
 * @Class  Name : ArticleManageController.java
 * @Description : [관리자] 게시판 게시글의 추가,수정, 조회, 삭제 등의 관리 작업
 * @Modification  Information
 *
 * @    수정일       수정자                                 수정내용
 * @ ----------   --------  ------------------------------------------------------------
 * @ 2024.07.22    정우민     최초 생성 ( 위펫 참고 )
 * @ 2025.12.30    수정       스프링 부트 형식으로 변경
 *
 *  @author 아람솔루션
 *  @since 2024.07.22
 *  @version 2.0
 *  @see
 */
@Slf4j
@RestController
@RequestMapping("/api/cont/code")
public class CmmCodeManageController {

	@Autowired
	private CmmCodeManageService cmmCodeManageService;

	// CommonService를 사용하여 setCommon 메서드 제공
	private CommonService commonService = new CommonService();

	protected void setCommon(HttpServletRequest request, ModelMap model) throws Exception {
		commonService.setCommon(request, model);
	}

	//공통분류코드 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectClCodeList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectClCodeList(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			List<Object> codeList = cmmCodeManageService.selectClCodeList(model);
			List<Object> codeDetailList = cmmCodeManageService.selectClCodeList(model);
			
			jsonMap.put("codeList", codeList);
			jsonMap.put("codeDetailList", codeDetailList);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	//대분류코드 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectCmmCodeList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectCmmCodeList(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int totalCount = cmmCodeManageService.selectCmmCodeCount(model);
			List<Object> data = cmmCodeManageService.selectCmmCodeList(model);
			
			jsonMap.put("data", data);
			jsonMap.put("recordsFiltered", totalCount);
			jsonMap.put("recordsTotal", totalCount);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    jsonMap.put("resultMessage", "공통코드 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
		}

        return jsonMap;
	}
	
	//대분류코드 엑셀 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectCmmCodeExcelList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectCmmCodeExcelList(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			List<Object> data = cmmCodeManageService.selectCmmCodeExcelList(model);
			
			jsonMap.put("data", data);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    jsonMap.put("resultMessage", "공통코드 엑셀 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
		}

        return jsonMap;
	}

	//대분류코드 등록 ajax
	@ResponseBody
	@PostMapping(value = "/insertCmmCode.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> insertCmmCode(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {

			// ====================== 대분류코드 중복 체크 실행 ======================
			String clCode = (String) model.get("clCode");
			String codeIdNum = (String) model.get("codeIdNum");
			
			if (clCode == null || clCode.trim().isEmpty()) {
				jsonMap.put("result", "01");
				jsonMap.put("message", "분류코드를 선택하세요");
				return jsonMap;
			}
			
			if (codeIdNum == null || codeIdNum.trim().isEmpty()) {
				jsonMap.put("result", "01");
				jsonMap.put("message", "코드ID를 입력하세요");
				return jsonMap;
			}
			
			String codeId = clCode + codeIdNum;
			model.put("codeId", codeId);

			String checkYn = cmmCodeManageService.checkCmmCodeDuplication(model);	// Y : 가능 N : 불가
			if( "Y".equals(checkYn) ) {
				jsonMap.put("data", cmmCodeManageService.insertCmmCode(model));
				jsonMap.put("message", "등록이 완료되었습니다.");
				jsonMap.put("result", "00");

			}else{
				jsonMap.put("result", "50");
				jsonMap.put("message", "중복되는 대분류 코드가 있습니다. 다른 코드ID 로 입력하세요");
			}

		} catch (Exception e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", "등록 중 오류가 발생했습니다: " + e.getMessage());
			e.printStackTrace();
		}

        return jsonMap;
	}

	//대분류코드 상세 조회 ajax
	@ResponseBody
    @PostMapping(value = "/selectCmmCodeDetail.Ajax", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> selectCmmCodeDetail(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
			jsonMap.put("data", cmmCodeManageService.selectCmmCodeDetail(model));
            jsonMap.put("result", "00");
		} catch (Exception e) {
			if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				e.printStackTrace();
				System.out.println("Connection Exception occurred");
			}
		}

        return jsonMap;
    }

	//대분류코드 수정 ajax
	@ResponseBody
	@PostMapping(value = "/updateCmmCode.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> updateCmmCode(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {

			// ====================== 대분류코드 중복 체크 실행 ======================
			String clCode = (String) model.get("clCode");
			String codeIdNum = (String) model.get("codeIdNum");
			
			if (clCode == null || clCode.trim().isEmpty()) {
				jsonMap.put("result", "01");
				jsonMap.put("message", "분류코드를 선택하세요");
				return jsonMap;
			}
			
			if (codeIdNum == null || codeIdNum.trim().isEmpty()) {
				jsonMap.put("result", "01");
				jsonMap.put("message", "코드ID를 입력하세요");
				return jsonMap;
			}
			
			String codeId = clCode + codeIdNum;
			model.put("codeId", codeId);
			
			jsonMap.put("data", cmmCodeManageService.updateCmmCode(model));
			jsonMap.put("message", "수정이 완료되었습니다.");
			jsonMap.put("result", "00");

			

		} catch (Exception e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", "수정 중 오류가 발생했습니다: " + e.getMessage());
			e.printStackTrace();
		}

        return jsonMap;
	}

	//대분류코드 삭제 ajax
	@ResponseBody
    @PostMapping(value = "/deleteCmmCode.Ajax", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> deleteCmmCode(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
			jsonMap.put("data", cmmCodeManageService.deleteCmmCode(model));
			jsonMap.put("result", "00");
			jsonMap.put("message", "삭제가 완료되었습니다.");

		} catch (Exception e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", "삭제 중 오류가 발생했습니다: " + e.getMessage());
			e.printStackTrace();
		}

        return jsonMap;
    }

	//소분류코드 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectCmmDetailCodeList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectCmmDetailCodeList(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int totalCount = cmmCodeManageService.selectCmmDetailCodeCount(model);
			List<Object> data = cmmCodeManageService.selectCmmDetailCodeList(model);
			jsonMap.put("data", data);
			jsonMap.put("recordsFiltered", totalCount);
			jsonMap.put("recordsTotal", totalCount);
			jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			e.printStackTrace();
		}

        return jsonMap;
	}
	
	//소분류코드 엑셀 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectCmmDetailCodeExcelList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectCmmDetailCodeExcelList(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			List<Object> data = cmmCodeManageService.selectCmmDetailCodeExcelList(model);
			jsonMap.put("data", data);
			jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			jsonMap.put("resultMessage", "소분류코드 엑셀 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
			e.printStackTrace();
		}

        return jsonMap;
	}

	//코드ID 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectCodeIdList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectCodeIdList(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			List<Object> data = cmmCodeManageService.selectCodeIdList(model);
			
			jsonMap.put("data", data);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    jsonMap.put("message", "코드ID 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
		}

        return jsonMap;
	}

	//소분류코드 등록 ajax
	@ResponseBody
	@PostMapping(value = "/insertCmmDetailCode.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> insertCmmDetailCode(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			String checkYn = cmmCodeManageService.checkCmmDetailCodeDuplication(model);	// Y : 가능 N : 불가
			if( "Y".equals(checkYn) ) {
				jsonMap.put("data", cmmCodeManageService.insertCmmDetailCode(model));
				jsonMap.put("message", "등록이 완료되었습니다.");
				jsonMap.put("result", "00");

			}else{
				jsonMap.put("result", "50");
				jsonMap.put("message", "중복되는 소분류 코드가 있습니다. 다른 코드ID 로 입력하세요");
			}
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    jsonMap.put("message", "소분류코드 등록 중 오류가 발생했습니다: " + e.getMessage());
		}

        return jsonMap;
	}

	//소분류코드 상세 조회 ajax
	@ResponseBody
    @PostMapping(value = "/selectCmmDetailCodeDetail.Ajax", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> selectCmmDetailCodeDetail(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
			jsonMap.put("data", cmmCodeManageService.selectCmmDetailCodeDetail(model));
            jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", "소분류코드 상세 조회 중 오류가 발생했습니다: " + e.getMessage());			
		}

        return jsonMap;
    }

	//소분류코드 수정 ajax
	@ResponseBody
    @PostMapping(value = "/updateCmmDetailCode.Ajax", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> updateCmmDetailCode(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
			jsonMap.put("data", cmmCodeManageService.updateCmmDetailCode(model));
            jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", "소분류코드 수정 중 오류가 발생했습니다: " + e.getMessage());			
		}

        return jsonMap;
    }

	//소분류코드 삭제 ajax
	@ResponseBody
    @PostMapping(value = "/deleteCmmDetailCode.Ajax", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> deleteCmmDetailCode(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
			jsonMap.put("data", cmmCodeManageService.deleteCmmDetailCode(model));
            jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", "소분류코드 삭제 중 오류가 발생했습니다: " + e.getMessage());			
		}

        return jsonMap;
    }

	/**
	 * 건축용도 코드 트리 (중분류 WAT001 + 소분류 WAT002를 children으로 묶음). REST API.
	 * GET /api/cont/code/building-use-codes
	 * - 성공: 200 + JSON 배열 [{ code, name, children: [{ code, name }, ...] }, ...]
	 */
	@GetMapping("/building-use-codes")
	public ResponseEntity<List<BuildingUseCodeTreeResponse>> getBuildingUseCodeList() {
		return ResponseEntity.ok(cmmCodeManageService.getBuildingUseCodeList());
	}

	/**
	 * 건축용도 코드 구분별 기준단가 조회 (WAT003)
	 * GET /api/cont/code/building-use-codes/unit-price?isOtherAct=true|false
	 */
	@GetMapping("/building-use-codes/unit-price")
	public ResponseEntity<List<DetailCodeResponse>> getBuildingUseCodeUnitPrice(
			@RequestParam(required = false, defaultValue = "false") Boolean isOtherAct) {
		return ResponseEntity.ok(cmmCodeManageService.getBuildingUseCodeUnitPrice(isOtherAct));
	}

	/**
	 * 소분류코드 리스트 조회 (USE_AT='Y', codeId 기준). REST API.
	 * GET /api/cont/code/{codeId}/details
	 * - 성공: 200 + List&lt;DetailCodeResponse&gt;
	 * - codeId 누락/잘못됨: 400 (GlobalExceptionHandler)
	 * - 서버 오류: 500 (GlobalExceptionHandler)
	 */
	@GetMapping("/{codeId}/details")
	public ResponseEntity<List<DetailCodeResponse>> getDetailCodeList(@PathVariable String codeId) {
		List<DetailCodeResponse> list = cmmCodeManageService.getDetailCodeListByCodeId(codeId);
		return ResponseEntity.ok(list);
	}

	/**
	 * 소분류코드 리스트 조회 (학교/학생 구분별 카테고리 매핑). REST API.
	 * GET /api/cont/code/{codeId}/details/by-category?studentCode=Y|E|J|H|O|T
	 * - studentCode 생략 시 중·고 전용 코드(01~04%)는 WHERE에서 제외됨 (기존 SQL 동작과 동일)
	 */
	@GetMapping("/{codeId}/details/by-category")
	public ResponseEntity<List<DetailCodeListByCategoryResponse>> getDetailCodeListByCategory(
			@PathVariable String codeId,
			@RequestParam(required = false) String studentCode) {
		return ResponseEntity.ok(cmmCodeManageService.getDetailCodeListByCategory(codeId, studentCode));
	}
}

