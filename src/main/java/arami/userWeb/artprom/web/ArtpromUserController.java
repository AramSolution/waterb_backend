package arami.userWeb.artprom.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import arami.adminWeb.artprom.service.ArtpromManageService;
import arami.adminWeb.artprom.service.dto.response.ArtprodScheduleListResponse;
import arami.shared.proc.dto.request.SelectList01Request;
import arami.shared.proc.dto.request.SelectList02Request;
import arami.shared.proc.dto.response.SelectList01ItemResponse;
import arami.shared.proc.dto.response.SelectList02ItemResponse;
import arami.shared.proc.service.ProcService;
import arami.userWeb.artprom.service.ArtpromUserService;
import arami.userWeb.artprom.service.dto.request.ArtpromUserDetailRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserFavoriteRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMainCardListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMyAppliedListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMyFavoriteListRequest;
import arami.userWeb.artprom.service.dto.response.ArtpromUserDetailResponse;
import arami.userWeb.artprom.service.dto.response.ArtpromUserFavoriteResponse;
import arami.userWeb.artprom.service.dto.response.ArtpromUserListDTO;
import arami.userWeb.artprom.service.dto.response.ArtpromUserListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 지원사업 조회 Controller (사용자웹)
 * 목록/상세 조회만 제공. 등록·수정·삭제는 관리자웹(adminWeb)에서만 처리.
 */
@Tag(name = "지원사업(사용자)", description = "사용자웹 - 지원사업 목록/상세 조회 API")
@RestController
@RequestMapping("/api/user/artprom")
@RequiredArgsConstructor
public class ArtpromUserController {

    private final ArtpromUserService artpromUserService;
    private final ArtpromManageService artpromManageService;
    private final ProcService procService;

    @Operation(summary = "지원사업 목록 조회", description = "페이징/검색 조건으로 지원사업 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromUserListResponse> selectArtpromList(@RequestBody ArtpromUserListRequest request) throws Exception {
        ArtpromUserListResponse response = new ArtpromUserListResponse();
        int totalCount = artpromUserService.selectArtpromListCount(request);
        List<ArtpromUserListDTO> data = artpromUserService.selectArtpromList(request);
        response.setData(data);
        response.setRecordsFiltered(totalCount);
        response.setRecordsTotal(totalCount);
        response.setResult("00");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상담일자별 상담장소/시간 목록", description = "f_selectlist02(proId, date) 호출. 공공형 진로진학 컨설팅 신청 화면 상담장소 SELECT용.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/{proId}/schedule-options", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<SelectList02ItemResponse>> getScheduleOptions(
            @PathVariable String proId,
            @RequestParam(name = "date") String date) throws Exception {
        SelectList02Request request = new SelectList02Request();
        request.setAProId(proId);
        request.setAWorkDt(date);
        List<SelectList02ItemResponse> list = procService.getSelectList02(request);
        return ResponseEntity.ok(list != null ? list : List.of());
    }

    @Operation(summary = "기준년월별 목록(selectlist01)", description = "f_selectlist01(proId, workYm) 호출.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/{proId}/list01-options", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<SelectList01ItemResponse>> getList01Options(
            @PathVariable String proId,
            @RequestParam(name = "workYm") String workYm) throws Exception {
        SelectList01Request request = new SelectList01Request();
        request.setAProId(proId);
        request.setAWorkYm(workYm);
        List<SelectList01ItemResponse> list = procService.getSelectList01(request);
        return ResponseEntity.ok(list != null ? list : List.of());
    }

    @Operation(summary = "지원사업 일정 + 신청 인원 목록 조회(회차관리)", description = "PRO_ID 기준 ARTPROD 일정 목록과 회차별 신청 인원/최대 인원 정보. 지역연계 진로체험활동 bizInfo 회차관리용.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/{proId}/schedule-with-apply", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtprodScheduleListResponse> getScheduleListWithApplyCnt(@PathVariable String proId) throws Exception {
        ArtprodScheduleListResponse response = new ArtprodScheduleListResponse();
        response.setData(artpromManageService.getScheduleListWithApplyCnt(proId));
        response.setResult("00");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지원사업 상세 조회", description = "상세 + 홍보파일(proFileList) + 첨부파일(fileList) 반환.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/{proId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromUserDetailResponse> selectArtpromDetail(@PathVariable String proId) throws Exception {
        ArtpromUserDetailRequest request = new ArtpromUserDetailRequest();
        request.setProId(proId);
        ArtpromUserDetailResponse response = artpromUserService.selectArtpromDetailResponse(request);
        response.setResult("00");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 신청/임시저장한 지원사업 목록", description = "MY PAGE 신청현황 - 로그인 사용자가 임시저장 또는 신청한 지원사업 목록. 비로그인 시 빈 목록.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/my-applied/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromUserListResponse> selectMyAppliedArtpromList(@RequestBody(required = false) ArtpromUserMyAppliedListRequest request) throws Exception {
        if (request == null) {
            request = new ArtpromUserMyAppliedListRequest();
        }
        ArtpromUserListResponse response = new ArtpromUserListResponse();
        int totalCount = artpromUserService.selectMyAppliedArtpromListCount(request);
        response.setData(artpromUserService.selectMyAppliedArtpromList(request));
        response.setRecordsFiltered(totalCount);
        response.setRecordsTotal(totalCount);
        response.setResult("00");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "멘토: 내가 신청/임시저장한 지원사업 목록", description = "MY PAGE 멘토 신청현황 — 멘토(ARTAPMM) 신청 1건당 1행. 멘토(MNR) 로그인 시에만 조회, 그 외·비로그인 시 빈 목록.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/my-applied/mentor/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromUserListResponse> selectMyAppliedArtpromListMentor(
            @RequestBody(required = false) ArtpromUserMyAppliedListRequest request) throws Exception {
        if (request == null) {
            request = new ArtpromUserMyAppliedListRequest();
        }
        ArtpromUserListResponse response = new ArtpromUserListResponse();
        int totalCount = artpromUserService.selectMyAppliedArtpromListMentorCount(request);
        response.setData(artpromUserService.selectMyAppliedArtpromListMentor(request));
        response.setRecordsFiltered(totalCount);
        response.setRecordsTotal(totalCount);
        response.setResult("00");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 즐겨찾기한 지원사업 목록", description = "MY PAGE 즐겨찾기 - 로그인 사용자가 즐겨찾기한 지원사업 목록. 비로그인 시 빈 목록.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/my-favorite/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromUserListResponse> selectMyFavoriteArtpromList(
            @RequestBody(required = false) ArtpromUserMyFavoriteListRequest request) throws Exception {
        if (request == null) {
            request = new ArtpromUserMyFavoriteListRequest();
        }
        ArtpromUserListResponse response = new ArtpromUserListResponse();
        int totalCount = artpromUserService.selectMyFavoriteArtpromListCount(request);
        response.setData(artpromUserService.selectMyFavoriteArtpromList(request));
        response.setRecordsFiltered(totalCount);
        response.setRecordsTotal(totalCount);
        response.setResult("00");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포털 메인 카드 목록", description = "/userWeb 포털 메인 카드 슬라이더용. REQ_GB/개수 제한 없이 조회.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/main-cards", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<ArtpromUserListDTO>> selectMainCards(
            @RequestParam(name = "includePromo", required = false) Boolean includePromo) throws Exception {
        ArtpromUserMainCardListRequest request = new ArtpromUserMainCardListRequest();
        request.setIncludePromo(includePromo);
        return ResponseEntity.ok(artpromUserService.selectArtpromMainCardList(request));
    }

    @Operation(summary = "지원사업 즐겨찾기 저장", description = "로그인 사용자(ESNTL_ID)와 지원사업(PRO_ID) 기준으로 기존 건 삭제 후 재등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "처리 성공"),
        @ApiResponse(responseCode = "401", description = "로그인 필요"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/favorite", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromUserFavoriteResponse> saveFavorite(
            @RequestBody ArtpromUserFavoriteRequest request) throws Exception {
        if (request == null || request.getProId() == null || request.getProId().isBlank()) {
            ArtpromUserFavoriteResponse badRequest = new ArtpromUserFavoriteResponse();
            badRequest.setResult("40");
            badRequest.setMessage("지원사업 코드가 필요합니다.");
            return ResponseEntity.badRequest().body(badRequest);
        }
        ArtpromUserFavoriteResponse response = artpromUserService.saveFavoriteArtprom(request);
        if ("41".equals(response.getResult())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지원사업 즐겨찾기 삭제", description = "로그인 사용자(ESNTL_ID)와 지원사업(PRO_ID) 기준으로 즐겨찾기를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "처리 성공"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패"),
        @ApiResponse(responseCode = "401", description = "로그인 필요"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/favorite/{proId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromUserFavoriteResponse> deleteFavorite(@PathVariable String proId) throws Exception {
        if (proId == null || proId.isBlank()) {
            ArtpromUserFavoriteResponse badRequest = new ArtpromUserFavoriteResponse();
            badRequest.setResult("40");
            badRequest.setMessage("지원사업 코드가 필요합니다.");
            return ResponseEntity.badRequest().body(badRequest);
        }
        ArtpromUserFavoriteResponse response = artpromUserService.deleteFavoriteArtprom(proId);
        if ("41".equals(response.getResult())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
