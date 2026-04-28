package arami.adminWeb.support.web;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import arami.adminWeb.support.service.SupportFeePayerManageService;
import arami.adminWeb.support.service.dto.request.SupportFeePayerBasicInfoRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerListRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerRegisterRequest;
import arami.adminWeb.support.service.dto.response.SupportFeePayerBasicUpdateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerCalculateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerListResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerRegisterResponse;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/admin/support/fee-payer")
public class SupportFeePayerManageController {

    @Resource(name = "supportFeePayerManageService")
    private SupportFeePayerManageService supportFeePayerManageService;

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    /**
     * 오수 원인자부담금 관리 목록 조회.
     * - ARTITED + ARTITEM 기준, ITEM_ID 당 SEQ 최대 1건(가장 최신 분)만 행으로 반환
     * - 납부(ARTITEP)는 해당 분 기준 최신 1건(납부일/납부액)만 조회
     * - 납부 이력이 없으면 납부일 null, 납부액 0
     */
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportFeePayerListResponse> list(@RequestBody(required = false) SupportFeePayerListRequest request) {
        SupportFeePayerListResponse response = new SupportFeePayerListResponse();
        try {
            SupportFeePayerListRequest actualRequest = request != null ? request : new SupportFeePayerListRequest();
            response.setData(supportFeePayerManageService.selectFeePayerList(actualRequest));
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.select"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("support fee-payer list error: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("오수 원인자부담금 관리 목록 조회 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 오수 원인자부담금 상세 조회 (ITEM_ID 기준).
     * - ARTITEM + ARTITED + ARTITEC 조합 조회
     * - CHG_USER_ID, CRT_DATE, CHG_DATE 제외 컬럼 응답
     */
    @GetMapping(value = "/{itemId}/detail", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportFeePayerDetailResponse> detail(@PathVariable("itemId") String itemId) {
        SupportFeePayerDetailResponse response = new SupportFeePayerDetailResponse();
        try {
            response.setData(supportFeePayerManageService.selectFeePayerDetail(itemId));
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.select"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("support fee-payer detail error: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("오수 원인자부담금 상세 조회 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 오수 원인자부담금 등록·수정(동일 URL).
     * - itemId 미전달: 신규 ARTITEM 채번 후 등록
     * - itemId 지정: ARTITEM 기본정보 수정 후 details 배열 단위로 ARTITED UPSERT, ARTITEC 동기화
     * - ARTITED: PK(ITEM_ID, SEQ) 기준 INSERT … ON DUPLICATE KEY UPDATE
     * - ARTITEC: 동일 SEQ 기존 행 삭제 후 계산 배열 순으로 SEQ2 부여·재등록, 건별 f_cost 반영
     */
    @PostMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportFeePayerRegisterResponse> register(
            @RequestBody @Valid SupportFeePayerRegisterRequest request) {
        try {
            SupportFeePayerRegisterResponse response = supportFeePayerManageService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("support fee-payer register: {}", e.getMessage());
            return ResponseEntity.ok(new SupportFeePayerRegisterResponse("40", e.getMessage(), null, List.of()));
        } catch (Exception e) {
            log.error("support fee-payer register error: {}", e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SupportFeePayerRegisterResponse("01", "오수 원인자부담금 등록·수정 중 오류가 발생했습니다.", null, List.of()));
        }
    }

    /**
     * 오수 원인자부담금 계산 버튼 전용 API.
     * - 등록/수정 API와 동일한 요청으로 선저장(프로시저/비용갱신 제외)
     * - 이후 대상 SEQ 1건 계산(f_cost + ARTITED 비용 갱신) 후 결과 반환
     */
    @PostMapping(value = "/calculate", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportFeePayerCalculateResponse> calculate(
            @RequestBody @Valid SupportFeePayerRegisterRequest request) {
        try {
            return ResponseEntity.ok(supportFeePayerManageService.calculateCost(request));
        } catch (IllegalArgumentException e) {
            log.warn("support fee-payer calculate: {}", e.getMessage());
            return ResponseEntity.ok(
                    new SupportFeePayerCalculateResponse("40", e.getMessage(), null, null, null, null, null, List.of()));
        } catch (Exception e) {
            log.error("support fee-payer calculate error: {}", e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SupportFeePayerCalculateResponse(
                            "01",
                            "오수 원인자부담금 계산 중 오류가 발생했습니다.",
                            null,
                            null,
                            null,
                            null,
                            null,
                            List.of()));
        }
    }

    /**
     * ARTITEM 기본정보 수정.
     * - ITEM_ID 로 존재 여부 확인 후 ENCRYPT 반영 업데이트
     */
    @PutMapping(value = "/{itemId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportFeePayerBasicUpdateResponse> updateBasic(
            @PathVariable("itemId") String itemId,
            @RequestBody @Valid SupportFeePayerBasicInfoRequest request) {
        try {
            return ResponseEntity.ok(supportFeePayerManageService.updateBasic(itemId, request));
        } catch (IllegalArgumentException e) {
            log.warn("support fee-payer basic update: {}", e.getMessage());
            return ResponseEntity.ok(new SupportFeePayerBasicUpdateResponse("40", e.getMessage(), itemId));
        } catch (Exception e) {
            log.error("support fee-payer basic update error: {}", e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SupportFeePayerBasicUpdateResponse(
                            "01",
                            "오수 원인자부담금 기본정보 수정 중 오류가 발생했습니다.",
                            itemId));
        }
    }
}
