package arami.userWeb.artedum.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import arami.adminWeb.artedum.service.ArtedumService;
import arami.adminWeb.artedum.service.dto.request.ArtedumListRequest;
import arami.adminWeb.artedum.service.dto.response.ArtedumDTO;
import arami.adminWeb.artedum.service.dto.response.ArtedumListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 가맹학원(희망사업 신청) 목록 조회 Controller (사용자웹)
 * 사용자웹 bizInfoPr 학원목록 모달에서 승인된 가맹학원 목록 조회.
 */
@Slf4j
@Tag(name = "가맹학원(사용자)", description = "사용자웹 - 승인된 가맹학원 목록 조회 API")
@RestController
@RequestMapping("/api/user/artedum")
public class ArtedumUserController {

    /** 승인된 가맹학원만 노출 (ARTEDUM.RUN_STA) */
    private static final String RUN_STA_APPROVED = "03";

    @Resource(name = "artedumService")
    private ArtedumService artedumService;

    @Operation(summary = "가맹학원 목록 조회", description = "승인된 가맹학원 목록 조회 (사용자웹 학원목록 모달용)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtedumListResponse> list(@RequestBody(required = false) ArtedumListRequest request) {
        if (request == null) {
            request = new ArtedumListRequest();
        }
        request.setRunSta(RUN_STA_APPROVED);
        request.setLength(request.getLength() != null && request.getLength() > 0 ? request.getLength() : 100);
        request.setDefaultPaging();

        ArtedumListResponse response = new ArtedumListResponse();
        int totalCount = artedumService.selectListCount(request);
        List<ArtedumDTO> data = artedumService.selectList(request);
        response.setData(data);
        response.setRecordsFiltered(totalCount);
        response.setRecordsTotal(totalCount);
        response.setResult("00");
        return ResponseEntity.ok(response);
    }
}
