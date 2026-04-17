package arami.common.adminWeb.menu.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.text.StringEscapeUtils;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import arami.common.CommonService;
import arami.common.adminWeb.menu.service.MenuMakeManageService;
import egovframework.com.cmm.EgovMessageSource;

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
@RequestMapping("/api/cont/menu")
public class MenuMakeManageController {

	@Autowired
    private EgovMessageSource egovMessageSource;

	@Autowired
	private MenuMakeManageService menuMakeManageService;

	// CommonService를 사용하여 setCommon 메서드 제공
	private CommonService commonService = new CommonService();

	protected void setCommon(HttpServletRequest request, ModelMap model) throws Exception {
		commonService.setCommon(request, model);
	}

	//메뉴 생성 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectMenuMakeAjax.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectMenuMakeAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			List<Object> data = menuMakeManageService.selectMenuMakeList(model);
			System.out.println("data: " + data);

			jsonMap.put("data", data);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	//메뉴 생성 내역 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectMenuCreatList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectMenuCreatList(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			List<EgovMap> data = menuMakeManageService.selectMenuCreatList(model);
			System.out.println("data: " + data);

			jsonMap.put("data", data);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	@ResponseBody
	@PostMapping(value = "/insertMenuCreatList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> insertMenuCreatList(
			@RequestBody HashMap<String, Object> requestBody,
			HttpServletRequest request, 
			ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			// 요청 파라미터 추출
			String authorCode = (String) requestBody.get("authorCode");
			@SuppressWarnings("unchecked")
			List<Object> menuNoObjList = (List<Object>) requestBody.get("menuNo");
			
			// menuNo가 null이거나 길이가 0이면 에러 반환
			if (menuNoObjList == null || menuNoObjList.isEmpty()) {
				jsonMap.put("result", "01");
				jsonMap.put("resultMessage", "선택된 메뉴가 없습니다.");
				return jsonMap;
			}
			
			// authorCode가 없으면 에러 반환
			if (authorCode == null || authorCode.isEmpty()) {
				jsonMap.put("result", "01");
				jsonMap.put("resultMessage", "권한코드가 없습니다.");
				return jsonMap;
			}
			
			// menuNo를 String으로 변환 (Integer 또는 String 모두 처리)
			List<String> menuNoList = new java.util.ArrayList<>();
			for (Object menuNoObj : menuNoObjList) {
				if (menuNoObj instanceof String) {
					menuNoList.add((String) menuNoObj);
				} else if (menuNoObj instanceof Integer) {
					menuNoList.add(String.valueOf((Integer) menuNoObj));
				} else if (menuNoObj instanceof Number) {
					menuNoList.add(String.valueOf(((Number) menuNoObj).intValue()));
				} else {
					menuNoList.add(String.valueOf(menuNoObj));
				}
			}
			
			// 서비스 호출
			menuMakeManageService.insertMenuCreatList(authorCode, menuNoList);
			
			jsonMap.put("result", "00");
			jsonMap.put("resultMessage", "정상적으로 저장되었습니다.");
		} catch (Exception e) {
			log.error("메뉴 생성 저장 실패", e);
		    jsonMap.put("result", "01");
		    jsonMap.put("resultMessage", "저장 중 오류가 발생했습니다: " + e.getMessage());
		}

        return jsonMap;
	}

	
	
}

