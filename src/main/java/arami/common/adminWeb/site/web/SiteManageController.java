package arami.common.adminWeb.site.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import arami.common.CommonService;
import arami.common.adminWeb.site.service.SiteManageService;
import egovframework.com.cmm.EgovMessageSource;

/**
 * @Class  Name : SiteManageController.java
 * @Description : [관리자] 사이트 마스터의 추가,수정, 조회, 삭제 등의 관리 작업
 * @Modification  Information
 *
 * @    수정일       수정자                                 수정내용
 * @ ----------   --------  ------------------------------------------------------------
 * @ 2025.12.30    수정       스프링 부트 형식으로 변경
 *
 *  @author 아람솔루션
 *  @since 2025.12.30
 *  @version 2.0
 *  @see
 */
@Slf4j
@RestController
@RequestMapping("/cont/site")
public class SiteManageController {

	@Autowired
    private EgovMessageSource egovMessageSource;

	@Autowired
	private SiteManageService siteManageService;
	
	// CommonService를 사용하여 setCommon 메서드 제공
	private CommonService commonService = new CommonService();

	protected void setCommon(HttpServletRequest request, ModelMap model) throws Exception {
		commonService.setCommon(request, model);
	}
 	
	@PostMapping(value = "/siteManage.adm")
	public Map<String, Object> siteManage(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		Map<String, Object> res = new HashMap<>();
		
		return res;
	}

	@PostMapping(value = "/insertSiteManage.adm")
    public Map<String, Object> insertSiteManage(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);
        Map<String, Object> res = new HashMap<>();
        return res;
    }

	@PostMapping(value = "/updateSiteManage.adm")
    public Map<String, Object> updateSiteManage(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);
        Map<String, Object> res = new HashMap<>();
        res.put("detail", siteManageService.selectSiteDetail(model));

        return res;
    }

	// 사이트 목록 조회
	@ResponseBody
	@PostMapping(value = "/selectSiteList.ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectSiteListAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();
		try {
			
			int totalCount = siteManageService.selectSiteListCount(model);
			List<EgovMap> list = siteManageService.selectSiteList(model);

			jsonMap.put("data", list);
            jsonMap.put("recordsFiltered", totalCount);
            jsonMap.put("recordsTotal", totalCount);
            jsonMap.put("resultCode", "0000");
		} catch (Exception e) {
		    jsonMap.put("resultCode", "0001");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 사이트 등록
	@ResponseBody
	@PostMapping(value = "/insertSite.ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> insertSiteAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int result = siteManageService.insertSite(model);
			jsonMap.put("data", result);
            jsonMap.put("resultCode", "0000");
		} catch (Exception e) {
		    jsonMap.put("resultCode", "0001");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 사이트 수정
	@ResponseBody
	@PostMapping(value = "/updateSite.ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> updateSiteAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int result = siteManageService.updateSite(model);
			jsonMap.put("data", result);
            jsonMap.put("resultCode", "0000");
		} catch (Exception e) {
		    jsonMap.put("resultCode", "0001");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 사이트 삭제
	@ResponseBody
	@PostMapping(value = "/deleteSite.ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> deleteSiteAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int result = siteManageService.deleteSite(model);
			jsonMap.put("data", result);
            jsonMap.put("resultCode", "0000");
		} catch (Exception e) {
		    jsonMap.put("resultCode", "0001");
		    e.printStackTrace();
		}

        return jsonMap;
	}

}