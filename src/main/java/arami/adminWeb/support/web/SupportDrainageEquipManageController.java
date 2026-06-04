package arami.adminWeb.support.web;



import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import arami.adminWeb.support.service.SupportDrainageEquipManageService;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipBasicInfoRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipListRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipPaymentDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipPaymentSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipRegisterRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipUnpaidListRequest;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipBasicUpdateResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDeleteResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDetailResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipExcelListResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipListResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentDetailResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentSaveResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipRegisterResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipUnpaidListResponse;
import arami.common.CommonService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;


@Slf4j
@Validated
@RestController
@RequestMapping("/api/admin/support/drainage-equip")
public class SupportDrainageEquipManageController extends CommonService {


    @Resource(name = "supportDrainageEquipManageService")
    private SupportDrainageEquipManageService supportDrainageEquipManageService;


    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;



    /**
     * 배수설비 관리 목록 조회.
     * - ARTEQUD + ARTEQUM 기준, ITEM_ID 당 SEQ 최대 1건(가장 최신 분)만 행으로 반환
     * - 납부(ARTEQUP)는 해당 분 기준 최신 1건(납부일/납부액)만 조회
     */

    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipListResponse> list(
            @RequestBody(required = false) SupportDrainageEquipListRequest request) {
        SupportDrainageEquipListResponse response = new SupportDrainageEquipListResponse();

        try {
            response = supportDrainageEquipManageService.selectDrainageEquipList(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.select"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("support drainage-equip list validation: {}", e.getMessage());
            response.setResult("01");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("support drainage-equip list error: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("배수설비 관리 목록 조회 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }



    /**
     * 배수설비 관리 목록 조회 (미납만).
     */

    @PostMapping(value = "/unpaid-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipUnpaidListResponse> unpaidList(
            @RequestBody @Valid SupportDrainageEquipUnpaidListRequest request) {
        SupportDrainageEquipUnpaidListResponse response = new SupportDrainageEquipUnpaidListResponse();

        try {
            response = supportDrainageEquipManageService.selectDrainageEquipUnpaidList(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.select"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("support drainage-equip unpaid-list validation: {}", e.getMessage());
            response.setResult("01");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("support drainage-equip unpaid-list error: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("배수설비 미납 목록 조회 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    /**

     * 배수설비 관리 목록 엑셀 조회.

     */

    @PostMapping(value = "/excel-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipExcelListResponse> excelList(
            @RequestBody(required = false) SupportDrainageEquipListRequest request) {
        SupportDrainageEquipExcelListResponse response = new SupportDrainageEquipExcelListResponse();

        try {
            SupportDrainageEquipListRequest actualRequest =
                    request != null ? request : new SupportDrainageEquipListRequest();
            response = supportDrainageEquipManageService.selectDrainageEquipExcelList(actualRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("support drainage-equip excel list error: {}", e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }



    /**
     * 배수설비 상세 조회 (ITEM_ID 기준).
     */

    @GetMapping(value = "/{itemId}/detail", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipDetailResponse> detail(@PathVariable("itemId") String itemId) {
        SupportDrainageEquipDetailResponse response = new SupportDrainageEquipDetailResponse();

        try {
            response.setData(supportDrainageEquipManageService.selectDrainageEquipDetail(itemId));
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.select"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("support drainage-equip detail error: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("배수설비 상세 조회 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }



    /**
     * 배수설비 납부 상세 조회 (ITEM_ID 기준).
     */

    @GetMapping(value = "/{itemId}/payment-detail", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipPaymentDetailResponse> paymentDetail(
            @PathVariable("itemId") String itemId) {
        SupportDrainageEquipPaymentDetailResponse response = new SupportDrainageEquipPaymentDetailResponse();

        try {
            response.setData(supportDrainageEquipManageService.selectDrainageEquipPaymentDetail(itemId));
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.select"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("support drainage-equip payment detail error: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("배수설비 납부 상세 조회 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    /**
     * 배수설비 납부내역 저장.
     * - 저장 완료 후 ARTEQUP.PAY 합계를 ARTEQUD.EQUIP_PAY에 반영
     * - EQUIP_PAY >= EQUIP_COST 이면 PAY_STA=02, 미만이면 01로 동기화
     */

    @PostMapping(value = "/payment", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipPaymentSaveResponse> savePayment(
            @RequestBody @Valid SupportDrainageEquipPaymentSaveRequest request) {

        try {
            return ResponseEntity.ok(
                    supportDrainageEquipManageService.saveDrainageEquipPayments(request, getCurrentUniqId()));
        } catch (IllegalArgumentException e) {
            log.warn("support drainage-equip payment save: {}", e.getMessage());
            return ResponseEntity.ok(new SupportDrainageEquipPaymentSaveResponse("40", e.getMessage(), null, null));
        } catch (Exception e) {
            log.error("support drainage-equip payment save error: {}", e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SupportDrainageEquipPaymentSaveResponse(
                            "01",
                            "배수설비 납부내역 저장 중 오류가 발생했습니다.",
                            null,
                            null));
        }
    }



    /**

     * 납부내역(ARTEQUP) 1건 삭제.

     */

    @DeleteMapping(value = "/payment/delete", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipDeleteResponse> deletePayment(
            @RequestBody @Valid SupportDrainageEquipPaymentDeleteRequest request) {

        try {
            return ResponseEntity.ok(
                    supportDrainageEquipManageService.deleteDrainageEquipPayment(request, getCurrentUniqId()));
        } catch (IllegalArgumentException e) {
            log.warn("support drainage-equip payment delete: {}", e.getMessage());
            return ResponseEntity.ok(new SupportDrainageEquipDeleteResponse("40", e.getMessage(), null, null));
        } catch (Exception e) {
            log.error("support drainage-equip payment delete error: {}", e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SupportDrainageEquipDeleteResponse(
                            "01",
                            "배수설비 납부내역 삭제 중 오류가 발생했습니다.",
                            null,
                            null));
        }
    }



    /**

     * 배수설비 목록 삭제.

     * - DB ARTEQUD.PAY_STA가 미납('01')일 때만 삭제 허용

     */

    @DeleteMapping(value = "/delete", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipDeleteResponse> deleteDetail(
            @RequestBody @Valid SupportDrainageEquipDeleteRequest request) {
        try {
            return ResponseEntity.ok(
                    supportDrainageEquipManageService.deleteDrainageEquipDetail(request, getCurrentUniqId()));
        } catch (IllegalArgumentException e) {
            log.warn("support drainage-equip detail delete: {}", e.getMessage());
            return ResponseEntity.ok(new SupportDrainageEquipDeleteResponse("40", e.getMessage(), null, null));
        } catch (Exception e) {
            log.error("support drainage-equip detail delete error: {}", e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SupportDrainageEquipDeleteResponse(
                            "01",
                            "배수설비 삭제 중 오류가 발생했습니다.",
                            null,
                            null));
        }
    }



    /**

     * 배수설비 등록·수정(동일 URL).

     */

    @PostMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipRegisterResponse> register(
            @RequestBody @Valid SupportDrainageEquipRegisterRequest request) {
        try {
            SupportDrainageEquipRegisterResponse response =
                    supportDrainageEquipManageService.register(request, getCurrentUniqId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("support drainage-equip register: {}", e.getMessage());
            return ResponseEntity.ok(new SupportDrainageEquipRegisterResponse("40", e.getMessage(), null));
        } catch (Exception e) {
            log.error("support drainage-equip register error: {}", e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SupportDrainageEquipRegisterResponse(
                            "01", "배수설비 등록·수정 중 오류가 발생했습니다.", null));
        }
    }



    /**

     * ARTEQUM 기본정보 수정.

     */

    @PutMapping(value = "/{itemId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<SupportDrainageEquipBasicUpdateResponse> updateBasic(
            @PathVariable("itemId") String itemId,
            @RequestBody @Valid SupportDrainageEquipBasicInfoRequest request) {
        try {
            return ResponseEntity.ok(
                    supportDrainageEquipManageService.updateBasic(itemId, request, getCurrentUniqId()));
        } catch (IllegalArgumentException e) {
            log.warn("support drainage-equip basic update: {}", e.getMessage());
            return ResponseEntity.ok(new SupportDrainageEquipBasicUpdateResponse("40", e.getMessage(), itemId));
        } catch (Exception e) {
            log.error("support drainage-equip basic update error: {}", e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SupportDrainageEquipBasicUpdateResponse(
                            "01",
                            "배수설비 기본정보 수정 중 오류가 발생했습니다.",
                            itemId));
        }
    }
}

