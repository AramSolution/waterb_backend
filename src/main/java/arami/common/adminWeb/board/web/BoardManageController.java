package arami.common.adminWeb.board.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import arami.common.CommonService;
import arami.common.adminWeb.board.service.BoardMasterService;
import arami.common.adminWeb.site.service.SiteManageService;
import egovframework.com.cmm.ComDefaultCodeVO;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovCmmUseService;
import egovframework.com.cmm.service.EgovProperties;
/**
 * @Class  Name : BoardManageController.java
 * @Description : [관리자] 게시판 마스터의 추가,수정, 조회, 삭제 등의 관리 작업
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
@RequestMapping("/api/cont/bord")
public class BoardManageController {

	@Autowired
    private EgovMessageSource egovMessageSource;

	@Autowired
	private BoardMasterService boardMasterService;
	
	@Autowired
	private SiteManageService siteManageService;
	
	@Autowired
	private EgovCmmUseService cmmUseService;

	// CommonService를 사용하여 setCommon 메서드 제공
	private CommonService commonService = new CommonService();

	protected void setCommon(HttpServletRequest request, ModelMap model) throws Exception {
		commonService.setCommon(request, model);
	}
	
	// 게시판관리 페이지/////
	@PostMapping(value = "/bbsMasterManage.adm")
		public Map<String, Object> bbsMasterManage(HttpServletRequest request, ModelMap model) throws Exception {
			Map<String, Object> res = new HashMap<>();
			this.setCommon(request, model);

			ComDefaultCodeVO vo = new ComDefaultCodeVO();
			//대상구분
			vo.setCodeId("EDR001");
			List<?> targetList = cmmUseService.selectCmmCodeDetail(vo);
			
			res.put("targetList", targetList);
			return res;
		}

	    // 게시판관리 게시판 등록 페이지
	@PostMapping(value = "/insertBbsMasterManage.adm")
		public Map<String, Object> insertBbsMasterManage(HttpServletRequest request, ModelMap model) throws Exception {
			this.setCommon(request, model);
			Map<String, Object> res = new HashMap<>();
			List<Object> data = boardMasterService.selectCmmCodeDetail(model);
			res.put("bbsSeList", data);

			List<EgovMap> siteList = siteManageService.selectSiteSelectList();
			res.put("siteList", siteList);
			//List<EgovMap> authList = authorityManageService.selectAuthorityList();

			//System.out.println("siteList : " + siteList);
			//System.out.println("authList : " + authList);

			res.put("siteList", siteList);
			//model.put("authList", authList);

			ComDefaultCodeVO vo = new ComDefaultCodeVO();
			//대상구분
			vo.setCodeId("EDR001");
			List<?> targetList = cmmUseService.selectCmmCodeDetail(vo);
			
			res.put("targetList", targetList);

			return res;
		}
		// 게시판관리 수정 페이지
	@PostMapping(value = "/updateBbsMasterManage.adm")
		public Map<String, Object> updateBbsMasterManage(HttpServletRequest request, ModelMap model) throws Exception {
			this.setCommon(request, model);
			Map<String, Object> res = new HashMap<>();
			
			// List<Object> data = boardMasterService.selectCmmCodeDetail(model);

			List<Object> data = boardMasterService.selectCmmCodeDetail(model);
			res.put("bbsSeList", data);

			res.put("siteList", siteManageService.selectSiteSelectList());
			
//			model.put("authList", authorityManageService.selectAuthorityList());
			res.put("detail", boardMasterService.selectBBSMasterDetail(model));
			
			ComDefaultCodeVO vo = new ComDefaultCodeVO();
			//대상구분
			vo.setCodeId("EDR001");
			List<?> targetList = cmmUseService.selectCmmCodeDetail(vo);
			
			res.put("targetList", targetList);

			return res;
		}
		
	// 게시판 목록 조회
	@ResponseBody
	@PostMapping(value = "/selectBBSMasterList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectBBSMasterListAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();
		try {
			System.out.println("targetGbn : " + model.get("targetGbn"));
			
			int totalCount = boardMasterService.selectBBSMasterListCount(model);
			List<Object> list = boardMasterService.selectBBSMasterList(model);

			jsonMap.put("data", list);
            jsonMap.put("recordsFiltered", totalCount);
            jsonMap.put("recordsTotal", totalCount);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 게시판 상세 조회
	@ResponseBody
	@PostMapping(value = "/selectBBSMasterDetail.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectBBSMasterDetailAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {

			EgovMap detail = boardMasterService.selectBoardMasterDetail(model);
			jsonMap.put("data", detail);
            jsonMap.put("resultCode", "000");
		} catch (Exception e) {
		    jsonMap.put("resultCode", "01");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 게시판 등록
	@ResponseBody
	@PostMapping(value = "/insertBBSMaster.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> insertBBSMasterAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			//다음 게시판ID 채번
			String bbsId = boardMasterService.getNextStringId(model);
			model.put("bbsId", bbsId);
			
			int result = boardMasterService.insertBBSMaster(model);
            jsonMap.put("result", "00");
            jsonMap.put("message", egovMessageSource.getMessage("success.common.insert"));
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    jsonMap.put("message", egovMessageSource.getMessage("fail.common.insert"));
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 게시판 수정
	@ResponseBody
	@PostMapping(value = "/updateBBSMaster.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> updateBBSMasterAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int result = boardMasterService.updateBBSMaster(model);
            jsonMap.put("result", "00");
			jsonMap.put("message", egovMessageSource.getMessage("success.common.update"));
		} catch (Exception e) {
		    jsonMap.put("result", "01");
			jsonMap.put("message", egovMessageSource.getMessage("fail.common.msg"));
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 게시판 삭제
	@ResponseBody
	@PostMapping(value = "/deleteBBSMaster.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> deleteBBSMasterAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int result = boardMasterService.deleteBBSMaster(model);
			jsonMap.put("result", "00");
			jsonMap.put("message", egovMessageSource.getMessage("success.common.delete"));
		} catch (Exception e) {
		    jsonMap.put("result", "01");
			jsonMap.put("message", egovMessageSource.getMessage("fail.common.msg"));
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 게시판 엑셀 리스트 조회
	@ResponseBody
	@PostMapping(value = "/selectBBSMasterExcelList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectBBSMasterExcelListAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			List<Object> data = boardMasterService.selectBBSMasterExcelList(model);
			jsonMap.put("data", data);
			jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				System.out.println("Connection Exception occurred");
				e.printStackTrace();
			}
		}

		return jsonMap;
	}

	/*
	 * 주석: 추가 기능들은 arami.v3 패키지의 클래스들이 필요할 수 있습니다.
	 * 해당 패키지가 제공되면 필요에 따라 추가하세요.
	 */
}
