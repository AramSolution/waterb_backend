package arami.adminWeb.armchil.web;

import arami.adminWeb.armchil.service.dto.response.ArmchilChildrenResponse;
import arami.shared.armchil.dto.request.ArmchilLinkRequest;
import arami.shared.armchil.dto.response.ArmchilChildDTO;
import arami.shared.armchil.service.ArmchilService;
import arami.shared.armuser.dto.response.ArmuserResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * ARMCHIL(자녀관리) 관리자웹 API - 부모별 자녀 목록 조회
 */
@RestController
@RequestMapping("/api/admin/armchil")
@RequiredArgsConstructor
public class ArmchilManageController {

    private final ArmchilService armchilService;

    /**
     * 부모(학부모) 고유ID에 따른 자녀 목록 조회.
     * 자녀별 학생명, 연락처, 성별, 생년월일, 주민번호, 프로필 사진(userPicFiles) 포함.
     * GET /api/admin/armchil/children?pEsntlId=xxx
     */
    @GetMapping("/children")
    public ResponseEntity<ArmchilChildrenResponse> getChildren(
            @RequestParam(name = "pEsntlId", required = true) String pEsntlId) {
        List<ArmchilChildDTO> list = armchilService.getChildrenByParent(pEsntlId);
        ArmchilChildrenResponse response = new ArmchilChildrenResponse(list, "00");
        return ResponseEntity.ok(response);
    }

    /**
     * 자녀(학생) 고유ID에 따른 학부모(보호자) 목록 조회.
     * 보호자별 이름, 연락처, 성별, 생년월일, 주민번호, 프로필 사진(userPicFiles) 포함.
     * GET /api/admin/armchil/parents?cEsntlId=xxx
     */
    @GetMapping("/parents")
    public ResponseEntity<ArmchilChildrenResponse> getParents(
            @RequestParam(name = "cEsntlId", required = true) String cEsntlId) {
        List<ArmchilChildDTO> list = armchilService.getParentsByChild(cEsntlId);
        ArmchilChildrenResponse response = new ArmchilChildrenResponse(list, "00");
        return ResponseEntity.ok(response);
    }

    /**
     * 부모(학부모) 고유ID에 따른 자녀 목록 조회 - 엑셀 (파일 미포함, RNUM·SEXDSTN_CODE_NM 포함).
     * GET /api/admin/armchil/children/excel?pEsntlId=xxx
     */
    @GetMapping("/children/excel")
    public ResponseEntity<ArmchilChildrenResponse> getChildrenExcel(
            @RequestParam(name = "pEsntlId", required = true) String pEsntlId) {
        List<ArmchilChildDTO> list = armchilService.getChildrenByParentExcel(pEsntlId);
        ArmchilChildrenResponse response = new ArmchilChildrenResponse(list, "00");
        return ResponseEntity.ok(response);
    }

    /**
     * 자녀 연동 등록 (관리자: 부모 ID 지정).
     * 학생명·성별·연락처·주민등록번호로 자녀 일치 후 ARMCHIL에 등록.
     * POST /api/admin/armchil/children?pEsntlId=xxx
     */
    @PostMapping("/children")
    public ResponseEntity<ArmuserResultResponse> linkChild(
            @RequestParam(name = "pEsntlId", required = true) String pEsntlId,
            @RequestBody @Valid ArmchilLinkRequest request) {
        return ResponseEntity.ok(armchilService.linkChild(pEsntlId, request));
    }

    /**
     * 자녀 연동 삭제 (부모·자식 ID로 ARMCHIL 1건 DELETE).
     * DELETE /api/admin/armchil/children/{pEsntlId}/{cEsntlId}
     */
    @DeleteMapping("/children/{pEsntlId}/{cEsntlId}")
    public ResponseEntity<Void> deleteChildLink(
            @PathVariable String pEsntlId,
            @PathVariable String cEsntlId) {
        armchilService.deleteChildLink(pEsntlId, cEsntlId);
        return ResponseEntity.noContent().build();
    }
}
