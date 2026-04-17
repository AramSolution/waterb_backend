package arami.common.adminWeb.program.web;

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
import arami.common.adminWeb.program.service.ProgramManageService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;
/**
 * @Class  Name : ProgramManageController.java
 * @Description : [관리자] 프로그램 추가,수정, 조회, 삭제 등의 관리 작업
 * @Modification  Information
 *
 * @    수정일       수정자                                 수정내용
 * @ ----------   --------  ------------------------------------------------------------
 * @ 2026.01.12    수정       최초 생성 ( 위펫 참고 )
 * 
 *  @author 아람솔루션
 *  @since 2026.01.12
 *  @version 1.0
 *  @see
 */
@Slf4j
@RestController
@RequestMapping("/api/cont/prog")
public class ProgramManageController {

	@Autowired
    private EgovMessageSource egovMessageSource;

	@Autowired
	private ProgramManageService programManageService;

	// CommonService를 사용하여 setCommon 메서드 제공
	private CommonService commonService = new CommonService();

	protected void setCommon(HttpServletRequest request, ModelMap model) throws Exception {
		commonService.setCommon(request, model);
	}
	
	// 프로그램관리 수정 페이지
	@PostMapping(value = "/updateProgramManage.adm")
	public Map<String, Object> updateProgramManage(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		Map<String, Object> res = new HashMap<>();
		res.put("detail", programManageService.selectProgramDetail(model));
		return res;
	}

	
	// 프로그램 등록 AJAX
	@ResponseBody
    @PostMapping(value = "/insertProgram.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> insertProgramAjax(HttpServletRequest request, ModelMap model) throws Exception {
		
		this.setCommon(request, model);
		HashMap<String, Object> res = new HashMap<>();

		try {
			String checkYn = programManageService.checkProgramIdAjax(model);  // Y : 가능 N : 불가
			if(checkYn.equals("Y")) {
				programManageService.insertProgramAjax(model);
				res.put("resultCode", "00");
				res.put("resultMessage", egovMessageSource.getMessage("commProg.0000"));
			}else {
				res.put("resultCode", "02");
				res.put("resultMessage", egovMessageSource.getMessage("commProg.0003"));
			}

		} catch (Exception e) {
			res.put("resultCode", "01");
			res.put("resultMessage", egovMessageSource.getMessage("commProg.0001"));
			e.printStackTrace();
		}

		
        return res;
	}

	// 프로그램 목록 조회 AJAX
	@ResponseBody
    @PostMapping(value = "/selectProgramList.Ajax", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> selectProgramList(HttpServletRequest request, ModelMap model) throws Exception {
	    this.setCommon(request, model);

        HashMap<String, Object> res = new HashMap<String,Object>();

        try {
            int totalCount = programManageService.selectProgramCount(model);

            res.put("data", programManageService.selectProgramList(model));
            res.put("recordsFiltered", totalCount);
            res.put("recordsTotal", totalCount);
            res.put("resultCode", "00");
            res.put("resultMessage", egovMessageSource.getMessage("error.code.0000"));
        }catch(Exception e) {
            res.put("data", "");
            res.put("resultCode", "01");
            res.put("resultMessage", egovMessageSource.getMessage("error.code.0001"));
            e.printStackTrace();
        }

        return res;
    }

	// 프로그램 수정 AJAX
	@ResponseBody
    @PostMapping(value = "/updateProgram.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> updateProgram(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> res = new HashMap<String,Object>();

		try {
			res.put("data", programManageService.updateProgramAjax(model));
            res.put("resultCode", "00");
            res.put("resultMessage", egovMessageSource.getMessage("error.code.0000"));
		} catch (Exception e) {
		    res.put("data", "");
            res.put("resultCode", "01");
            res.put("resultMessage", egovMessageSource.getMessage("error.code.0001"));
			e.printStackTrace();
		}

        return res;
	}

	// 프로그램 삭제 AJAX
	@ResponseBody
    @PostMapping(value = "/deleteProgram.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> deleteProgram(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> res = new HashMap<String,Object>();

		try {
			res.put("data", programManageService.deleteProgramAjax(model));
            res.put("resultCode", "00");
            res.put("resultMessage", egovMessageSource.getMessage("error.code.0000"));
		} catch (Exception e) {
		    res.put("data", "");
            res.put("resultCode", "01");
            res.put("resultMessage", egovMessageSource.getMessage("error.code.0001"));
            e.printStackTrace();
		}

        return res;
	}

	// 프로그램 엑셀 리스트 조회
	@ResponseBody
    @PostMapping(value = "/selectProgramExcelList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectProgramExcelListAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();
		try {
			List<Object> data = programManageService.selectProgramExcelList(model);
			jsonMap.put("data", data);
			jsonMap.put("resultCode", "00");
			jsonMap.put("resultMessage", egovMessageSource.getMessage("error.code.0000"));
		} catch (Exception e) {
			jsonMap.put("resultCode", "01");
			jsonMap.put("resultMessage", egovMessageSource.getMessage("error.code.0001"));
			if (egovframework.com.cmm.service.EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
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
