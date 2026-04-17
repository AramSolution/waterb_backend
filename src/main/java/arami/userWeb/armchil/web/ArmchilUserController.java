package arami.userWeb.armchil.web;

import arami.common.CommonService;
import arami.shared.armchil.dto.request.ArmchilLinkRequest;
import arami.shared.armchil.dto.response.ArmchilChildDTO;
import arami.shared.armchil.service.ArmchilService;
import arami.shared.armuser.dto.response.ArmuserResultResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ARMCHIL(자녀관리) 사용자웹 API - 로그인한 학부모의 자녀 목록 조회
 */
@RestController
@RequestMapping("/api/user/armchil")
public class ArmchilUserController extends CommonService {

    @Resource
    private ArmchilService armchilService;

    /**
     * 로그인한 학부모의 자녀 목록 조회.
     * 자녀별 학생명, 연락처, 성별, 생년월일, 주민번호, 프로필 사진(userPicFiles) 포함.
     * GET /api/user/armchil/children
     */
    @GetMapping("/children")
    public ResponseEntity<Map<String, Object>> getChildren() {
        String pEsntlId = getCurrentUniqId();
        List<ArmchilChildDTO> list = armchilService.getChildrenByParent(pEsntlId);
        Map<String, Object> body = new HashMap<>();
        body.put("data", list);
        body.put("result", "00");
        return ResponseEntity.ok(body);
    }

    /**
     * 로그인한 학부모의 자녀 목록 조회 - 엑셀 (파일 미포함, RNUM·SEXDSTN_CODE_NM 포함).
     * GET /api/user/armchil/children/excel
     */
    @GetMapping("/children/excel")
    public ResponseEntity<Map<String, Object>> getChildrenExcel() {
        String pEsntlId = getCurrentUniqId();
        List<ArmchilChildDTO> list = armchilService.getChildrenByParentExcel(pEsntlId);
        Map<String, Object> body = new HashMap<>();
        body.put("data", list);
        body.put("result", "00");
        return ResponseEntity.ok(body);
    }

    /**
     * 로그인한 학생의 보호자 목록 조회 (내 보호자 목록).
     * 로그인 사용자 == 해당 학생인 경우만 사용. 보호자별 이름, 연락처, 성별, 생년월일, 주민번호, 프로필 사진(userPicFiles) 포함.
     * GET /api/user/armchil/parents
     */
    @GetMapping("/parents")
    public ResponseEntity<Map<String, Object>> getParents() {
        String cEsntlId = getCurrentUniqId();
        List<ArmchilChildDTO> list = armchilService.getParentsByChild(cEsntlId);
        Map<String, Object> body = new HashMap<>();
        body.put("data", list);
        body.put("result", "00");
        return ResponseEntity.ok(body);
    }

    /**
     * 자녀 연동 등록 (로그인한 학부모 = 부모).
     * 학생명·성별·연락처·주민등록번호로 자녀 일치 후 ARMCHIL에 등록.
     * POST /api/user/armchil/children
     */
    @PostMapping("/children")
    public ResponseEntity<ArmuserResultResponse> linkChild(@RequestBody @Valid ArmchilLinkRequest request) {
        String pEsntlId = getCurrentUniqId();
        return ResponseEntity.ok(armchilService.linkChild(pEsntlId, request));
    }

    /**
     * 자녀 연동 삭제 (로그인한 학부모 = 부모, 자식 ID만 path).
     * DELETE /api/user/armchil/children/{cEsntlId}
     */
    @DeleteMapping("/children/{cEsntlId}")
    public ResponseEntity<Void> deleteChildLink(@PathVariable String cEsntlId) {
        String pEsntlId = getCurrentUniqId();
        armchilService.deleteChildLink(pEsntlId, cEsntlId);
        return ResponseEntity.noContent().build();
    }
}
