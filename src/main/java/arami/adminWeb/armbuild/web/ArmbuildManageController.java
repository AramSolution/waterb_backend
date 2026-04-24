package arami.adminWeb.armbuild.web;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;

import arami.adminWeb.armbuild.service.ArmbuildManageService;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildInsertBatchRequest;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildListRequest;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildInsertBatchResponse;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildListResponse;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildResultResponse;

/**
 * 건축물용도(ARMBULD) 관리 API (응답: result, message — Artapps와 동일 패턴)
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/admin/armbuild")
public class ArmbuildManageController {

	@Resource(name = "armbuildManageService")
	private ArmbuildManageService armbuildManageService;

	@Resource(name = "egovMessageSource")
	private EgovMessageSource egovMessageSource;

	/**
	 * 건축물용도 목록 조회 (GUBUN1=중분류 code, GUBUN2=소분류 code).
	 * GET /api/admin/armbuild?gubun1=01&gubun2=0101
	 */
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<ArmbuildListResponse> list(@Valid @ModelAttribute ArmbuildListRequest request) {
		ArmbuildListResponse response = new ArmbuildListResponse();
		try {
			response.setData(armbuildManageService.selectArmbuildList(request));
			response.setResult("00");
			response.setMessage(egovMessageSource.getMessage("success.common.select"));
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("armbuild list: {}", e.getMessage(), e);
			response.setResult("01");
			response.setMessage("건축물용도 목록 조회 중 오류가 발생했습니다.");
			if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
				e.printStackTrace();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * 건축물용도 일괄 저장 (items: 신규는 buildId 생략·수정은 buildId 포함).
	 * POST /api/admin/armbuild
	 * Body: JSON { "items": [ { "gubun1", "gubun2", "buildNm", "dayVal?", "buildDesc?", "buildId?" }, ... ] }
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json;charset=UTF-8")
	public ResponseEntity<ArmbuildInsertBatchResponse> insert(@RequestBody @Valid ArmbuildInsertBatchRequest batch) {
		try {
			return ResponseEntity.ok(armbuildManageService.saveArmbuildBatch(batch));
		} catch (IllegalArgumentException e) {
			log.warn("armbuild batch save: {}", e.getMessage());
			ArmbuildInsertBatchResponse response = new ArmbuildInsertBatchResponse();
			response.setResult("40");
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("armbuild batch save: {}", e.getMessage(), e);
			ArmbuildInsertBatchResponse response = new ArmbuildInsertBatchResponse();
			response.setResult("01");
			response.setMessage("건축물용도 저장 중 오류가 발생했습니다.");
			if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
				e.printStackTrace();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * 건축물용도 논리 삭제 (STTUS_CODE = 'D').
	 * DELETE /api/admin/armbuild/{buildId}
	 */
	@DeleteMapping(value = "/{buildId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<ArmbuildResultResponse> delete(@PathVariable String buildId) {
		try {
			return ResponseEntity.ok(armbuildManageService.deleteArmbuild(buildId));
		} catch (Exception e) {
			log.error("armbuild delete buildId={}: {}", buildId, e.getMessage(), e);
			ArmbuildResultResponse response = new ArmbuildResultResponse();
			response.setResult("01");
			response.setMessage("건축물용도 삭제 중 오류가 발생했습니다.");
			if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
				e.printStackTrace();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
