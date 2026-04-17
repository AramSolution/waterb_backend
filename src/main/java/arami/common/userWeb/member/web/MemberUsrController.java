package arami.common.userWeb.member.web;

import arami.common.CommonService;
import arami.common.userWeb.member.service.MemberUsrService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.service.EgovProperties;
import egovframework.let.utl.sim.service.EgovFileScrty;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberUsrController extends CommonService {

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Resource
	private MemberUsrService memberUsrService;

    /* 사용이유 : Egov 자동코드 생성 */
	@Resource(name = "egovUsrCnfrmIdGnrService")
	private EgovIdGnrService idgenService;
    
    @PostMapping(value = "/memberList.Ajax", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> selectMemberList(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        // System.out.println("userSe: " + model.get("userSe"));
        try {
            int totalCount = memberUsrService.selectUserMemberListCount(model);
			resultMap.put("data", memberUsrService.selectUserMemberList(model));
            System.out.println("data: " + memberUsrService.selectUserMemberList(model));
			resultMap.put("recordsFiltered", totalCount);
			resultMap.put("recordsTotal", totalCount);

            resultMap.put("result", "00");
        }catch(Exception e) {
            log.error("Error in selectMemberList: " + e.getMessage(), e);
            resultMap.put("result", "01");
        }

        return resultMap;
    }

    // ========================================
	// (AJAX|INSERT) 관리자회원 등록 AJAX
	// ========================================
	@ResponseBody
    @PostMapping(value = "/insertAdminUserMember.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> insertAdminUserMember(HttpServletRequest request, ModelMap model) throws Exception {

		/** (초기설정) */
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {

			// ====================== 아이디 중복 체크 실행 ======================
			String checkYn = memberUsrService.checkMemberId(model);	// Y : 가능 N : 불가

			if( "Y".equals(checkYn) ) {


				/** [1] 코드채번 + SHA256 */
				String esntlId = idgenService.getNextStringId();
				// EgovFramework 자동 코드 생성
				String newPw    = EgovFileScrty.encryptPassword(model.get("password").toString(), model.get("userId").toString());

				//String detailAdres = URLDecoder.decode(request.getParameter("detailAdres"), StandardCharsets.UTF_8.name());
				//System.out.println("detailAdres ::" +detailAdres);

				// // Egov 비밀번호 암호화 사용
				// if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				// 	System.out.println("[system.out] esntlId : " + esntlId);
				// 	System.out.println("[system.out] newPw    : " + newPw);
				// }

				// [2] DB 가공
				model.put("esntlId"  , esntlId);
				model.put("newPw"     , newPw );
				//model.put("detailAdres"     , detailAdres );
				//model.put("brthdy" , model.get("brthdy").toString().replaceAll("-", ""));

				//[3-1] 하드코딩
				model.put("passwordHint"  , "P01"     );	// 비밀번호 힌트 질문 : 가장 기억에 남는 장소는 ?
				model.put("passwordCnsr"  , "아람솔루션");		// 비밀번호 힌트 정답

				// [3-2] 화면에서 사용자가 숫자를 제대로 입력하지 못할 가능성을 대비 초기값 0 으로 설정
				//model.put("telNoArea"     , "000");			// model.put("telNoArea"     , model.get("mbtlNum").toString().split("-")[0]);
				//model.put("telNoMid"     , "0000");			// model.put("telNoMid"      , model.get("mbtlNum").toString().split("-")[1]);
				//model.put("telNoLast"     , "0000");		// model.put("telNoLast"     , model.get("mbtlNum").toString().split("-")[2]);


				// [4] 회원 등록 시 권한 부여 UserJoinController 참고
				model.put("authCode", "ROLE_ADMIN");
				model.put("mbrTypeCd", "USR03");
				int authResult = memberUsrService.insertAdminRollAjax(model);

				// 회원종류코드
				model.put("groupId", "GROUP_00000000000000");
				// 회원종류코드
				model.put("userSe", "USR");
				// 회원유형코드
				//model.put("memberTypeCd", "A_MEM");

				// 관리자 일반정보 등록
				int result1 = memberUsrService.insertAdminMemberAjax(model);

				jsonMap.put("result", "00");
				jsonMap.put("message", "등록이 완료되었습니다.");
				if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
					//System.out.println("[system.out] 등록 처리 결과 : " + result);
				}

			}else{
				jsonMap.put("result", "50");
				jsonMap.put("message", "중복되는 아이디가 있습니다. 다른 아이디를 사용하여 주십시요.");
			}

		} catch (Exception e) {
			jsonMap.put("result", "01");
			if (EgovProperties.getProperty("Globals.debug").toString().equals("true")) {
				System.out.println("Connection Exception occurred");
				e.printStackTrace();
			}
		}

		return jsonMap;
	}

}