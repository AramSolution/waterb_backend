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
import arami.common.adminWeb.menu.service.MenuTreeManageService;
import arami.common.adminWeb.menu.service.MenuManageService;
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
public class MenuTreeManageController {

	@Autowired
    private EgovMessageSource egovMessageSource;

	@Autowired
	private MenuTreeManageService menuTreeManageService;

	@Autowired
	private MenuManageService menuManageService;

	// CommonService를 사용하여 setCommon 메서드 제공
	private CommonService commonService = new CommonService();

	protected void setCommon(HttpServletRequest request, ModelMap model) throws Exception {
		commonService.setCommon(request, model);
	}

	//메뉴관리 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/menuTreeManage.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> menuTreeManageAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			List<EgovMap> data = menuManageService.selectMenuListT_D();
			System.out.println("data: " + data);

			jsonMap.put("data", data);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 메뉴 상세 조회 AJAX
	@ResponseBody
	@PostMapping(value = "/selectMenuTreeDetailAjax.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectMenuTreeDetailAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {

			jsonMap.put("data", menuTreeManageService.selectMenuDetail(model));
			jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			e.printStackTrace();
		}

		return jsonMap;
	}

	// 메뉴 등록 AJAX
	@ResponseBody
    @PostMapping(value = "/insertMenuTreeAjax.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> insertMenuTreeAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			// ====================== 메뉴 중복 체크 실행 ======================
			String checkYn = menuTreeManageService.checkMenuIdAjax(model);	// Y : 가능 N : 불가
			if( "Y".equals(checkYn) ) {
				int insert = menuTreeManageService.insertMenuAjax(model);
				jsonMap.put("result", "00");
				jsonMap.put("message", "메뉴 등록이 완료되었습니다.");
			}else{
				jsonMap.put("result", "50");
				jsonMap.put("message", "중복되는 메뉴명이 있습니다. 수정하여 사용해주세요.");
			}

		} catch (Exception e) {
			jsonMap.put("result", "01");
			e.printStackTrace();
		}

		return jsonMap;
	}

	// 메뉴 수정 AJAX
	@ResponseBody
    @PostMapping(value = "/updateMenuTreeAjax.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> updateMenuTreeAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int update = menuTreeManageService.updateMenuAjax(model);
			jsonMap.put("result", "00");
			jsonMap.put("message", "메뉴 수정이 완료되었습니다.");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			e.printStackTrace();
		}

		return jsonMap;
	}

	// 메뉴 삭제 AJAX
	@ResponseBody
	@PostMapping(value = "/deleteMenuTreeAjax.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> deleteMenuTreeAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			jsonMap.put("data", menuTreeManageService.deleteMenuAjax(model));
			jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			e.printStackTrace();
		}

		return jsonMap;
	}

	
}
