package arami.userWeb.artappm.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import arami.common.CommonService;
import arami.common.files.FileUtil;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import arami.adminWeb.artadvi.service.ArtadviManageService;
import arami.adminWeb.artadvi.service.dto.request.ArtadviListRequest;
import arami.adminWeb.artadvi.service.dto.request.ArtadviSaveRequest;
import arami.adminWeb.artadvi.service.dto.response.ArtadviDTO;
import arami.userWeb.artappm.dto.MentorInfoSaveRequest;
import arami.userWeb.artappm.dto.RejectSaveRequest;
import arami.userWeb.artappm.dto.UserMentorApplicationRegisterRequest;
import arami.adminWeb.artappm.service.dto.response.ArtapmmDuplicateCheckResponse;
import arami.adminWeb.artappm.service.ArtappmManageService;
import arami.adminWeb.artappm.service.dto.request.ArtappmByStudentRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmDetailRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmFileDeleteRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmInsertRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmUpdateRequest;
import arami.adminWeb.artappm.service.dto.request.StudyCertDetailRequest;
import arami.adminWeb.artappm.service.dto.request.StudyCertUploadRequest;
import arami.adminWeb.artappm.service.dto.response.ArtapmmApplicationListItemResponse;
import arami.adminWeb.artappm.service.dto.response.ArtappmDTO;
import arami.adminWeb.artappm.service.dto.response.ArtappmResultResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertDetailApiResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertDetailResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertListItemResponse;
import arami.adminWeb.artappm.service.dto.response.ChangeListItemResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertListResponse;
import arami.adminWeb.artapps.service.ArtappsManageService;
import arami.adminWeb.artapps.service.dto.request.ArtappsInsertRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsUpdateRequest;
import arami.adminWeb.artapps.service.dto.response.ArtappsResultResponse;

/**
 * 지원사업 신청 Controller (사용자웹)
 * 사용자웹 /userWeb/bizInput 등에서 지원사업 신청 등록만 제공.
 */
@Slf4j
@Tag(name = "지원사업 신청(사용자)", description = "사용자웹 - 지원사업 신청 등록 API")
@RestController
@RequestMapping("/api/user/artappm")
public class ArtappmUserController extends CommonService {

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Resource
    private ArtappmManageService artappmManageService;

    @Resource
    private ArtadviManageService artadviManageService;

    @Resource
    private ArtappsManageService artappsManageService;

    @Resource(name = "fileUtil")
    private FileUtil fileUtil;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    /** PRO_GB=02(사전지원)일 때 첨부파일 seq별 FILE_DESC */
    private static final Map<Integer, String> ARTAPPM_FILE_DESC_BY_SEQ = Map.of(
            1, "신청서",
            2, "개인정보수집 동의서",
            3, "신청자 점수 산정표",
            4, "신분증",
            5, "통장사본"
    );

    private static void applyFileDescForProGb02(List<FileDTO> fileList, String proGb) {
        if (!"02".equals(proGb) || fileList == null) {
            return;
        }
        for (FileDTO fileInfo : fileList) {
            String desc = ARTAPPM_FILE_DESC_BY_SEQ.get(fileInfo.getSeq());
            if (desc != null) {
                fileInfo.setFileDesc(desc);
            }
        }
    }

    /** 수강확인증 업로드일자 파싱. yyyy-MM-dd HH:mm:ss 또는 yyyy-MM-dd. 실패 시 null(서버 NOW() 사용). */
    private static Date parseUploadDttm(String uploadDttm) {
        if (!StringUtils.hasText(uploadDttm)) {
            return null;
        }
        String s = uploadDttm.trim();
        try {
            if (s.length() > 10) {
                return Date.from(LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .atZone(ZoneId.systemDefault()).toInstant());
            }
            return Date.from(LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Operation(summary = "학생·사업별 최신 신청 조회", description = "bizInput 학생 선택 시 기존 신청 데이터 조회. fileId 있으면 fileList 포함.")
    @GetMapping("/by-student")
    public ResponseEntity<Map<String, Object>> getByStudent(
            @RequestParam String proId,
            @RequestParam String reqEsntlId) throws Exception {
        ArtappmByStudentRequest request = new ArtappmByStudentRequest();
        request.setProId(proId);
        request.setReqEsntlId(reqEsntlId);
        request.setPEsntlId(getCurrentUniqId());
        ArtappmDTO detail = artappmManageService.selectArtappmByStudentAndPro(request);
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("detail", detail);
        body.put("result", "00");
        if (detail != null && detail.getFileId() != null && !detail.getFileId().trim().isEmpty()) {
            try {
                Long fileId = Long.parseLong(detail.getFileId().trim());
                List<FileDTO> fileDtoList = fileManageService.selectFileListByFileId(fileId);
                List<Map<String, Object>> fileList = new ArrayList<>();
                for (FileDTO f : fileDtoList) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fileId", String.valueOf(f.getFileId()));
                    m.put("seq", f.getSeq());
                    m.put("orgfNm", f.getOrgfNm());
                    fileList.add(m);
                }
                body.put("fileList", fileList);
            } catch (NumberFormatException e) {
                log.warn("getByStudent: invalid fileId, skip fileList. fileId={}", detail.getFileId());
            }
        }
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "신청 상세 조회(reqId)", description = "REQ_ID 기준 신청 상세 조회. fileId 있으면 fileList 포함. (MY PAGE reqId 기반 로딩용)")
    @GetMapping("/by-req-id/{reqId}")
    public ResponseEntity<Map<String, Object>> getByReqId(@PathVariable String reqId) throws Exception {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("result", "00");
        if (!StringUtils.hasText(reqId)) {
            body.put("detail", null);
            return ResponseEntity.ok(body);
        }

        ArtappmDetailRequest request = new ArtappmDetailRequest();
        request.setReqId(reqId.trim());
        ArtappmDTO detail = artappmManageService.selectArtappmDetail(request);

        if (detail == null) {
            body.put("detail", null);
            return ResponseEntity.ok(body);
        }

        String currentEsntlId = getCurrentUniqId();
        String reqEsntl = detail.getReqEsntlId() != null ? detail.getReqEsntlId().trim() : "";
        String pEsntl = detail.getPEsntlId() != null ? detail.getPEsntlId().trim() : "";
        if (StringUtils.hasText(currentEsntlId)) {
            String cur = currentEsntlId.trim();
            if (!cur.equals(reqEsntl) && !cur.equals(pEsntl)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        body.put("detail", detail);
        if (detail.getFileId() != null && !detail.getFileId().trim().isEmpty()) {
            try {
                Long fileId = Long.parseLong(detail.getFileId().trim());
                List<FileDTO> fileDtoList = fileManageService.selectFileListByFileId(fileId);
                List<Map<String, Object>> fileList = new ArrayList<>();
                for (FileDTO f : fileDtoList) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fileId", String.valueOf(f.getFileId()));
                    m.put("seq", f.getSeq());
                    m.put("orgfNm", f.getOrgfNm());
                    fileList.add(m);
                }
                body.put("fileList", fileList);
            } catch (NumberFormatException e) {
                log.warn("getByReqId: invalid fileId, skip fileList. fileId={}", detail.getFileId());
            }
        }
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "멘토 신청 상세 조회(reqId)", description = "ARTAPMM REQ_ID 기준 멘토 신청 상세. fileId 있으면 fileList 포함. 로그인 시 신청자(REQ_ESNTL_ID) 본인만 조회, 그 외 403. 논리삭제(STTUS_CODE=D) 건은 조회되지 않음.")
    @GetMapping("/mentor-application/by-req-id/{reqId}")
    public ResponseEntity<Map<String, Object>> getMentorApplicationByReqId(@PathVariable String reqId) throws Exception {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("result", "00");
        if (!StringUtils.hasText(reqId)) {
            body.put("detail", null);
            return ResponseEntity.ok(body);
        }

        ArtapmmApplicationListItemResponse detail = artappmManageService.getArtapmmMentorApplicationDetailByReqId(reqId.trim());
        if (detail == null) {
            body.put("detail", null);
            return ResponseEntity.ok(body);
        }

        String currentEsntlId = getCurrentUniqId();
        String reqEsntl = detail.getReqEsntlId() != null ? detail.getReqEsntlId().trim() : "";
        if (StringUtils.hasText(currentEsntlId)) {
            String cur = currentEsntlId.trim();
            if (!cur.equals(reqEsntl)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        body.put("detail", detail);
        if (detail.getFileId() != null && !detail.getFileId().trim().isEmpty()) {
            try {
                Long fileId = Long.parseLong(detail.getFileId().trim());
                List<FileDTO> fileDtoList = fileManageService.selectFileListByFileId(fileId);
                List<Map<String, Object>> fileList = new ArrayList<>();
                for (FileDTO f : fileDtoList) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fileId", String.valueOf(f.getFileId()));
                    m.put("seq", f.getSeq());
                    m.put("orgfNm", f.getOrgfNm());
                    fileList.add(m);
                }
                body.put("fileList", fileList);
            } catch (NumberFormatException e) {
                log.warn("getMentorApplicationByReqId: invalid fileId, skip fileList. fileId={}", detail.getFileId());
            }
        }
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "멘토정보 조회(진로진학 상담)", description = "REQ_ID 기준 ARTADVI 상담 1건 조회. 상담장소·시간·내용·첨부파일 반환. 사유(REA_DESC)는 BY_STUDENT detail.reaDesc 사용.")
    @GetMapping(value = "/mentor-info", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtadviDTO> getMentorInfo(@RequestParam String reqId) {
        try {
            ArtadviListRequest request = new ArtadviListRequest();
            request.setReqId(reqId);
            List<ArtadviDTO> list = artadviManageService.selectArtadviList(request);
            if (list == null || list.isEmpty()) {
                return ResponseEntity.ok(null);
            }
            ArtadviDTO dto = list.get(0);
            String fileIdStr = dto.getFileId();
            if (fileIdStr != null && !fileIdStr.isBlank()) {
                try {
                    Long fileId = Long.parseLong(fileIdStr.trim());
                    List<FileDTO> fileList = fileManageService.selectFileListByFileId(fileId);
                    dto.setFiles(fileList != null ? fileList : new ArrayList<>());
                } catch (NumberFormatException e) {
                    log.warn("getMentorInfo: invalid fileId={}", fileIdStr);
                    dto.setFiles(new ArrayList<>());
                }
            } else {
                dto.setFiles(new ArrayList<>());
            }
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("getMentorInfo: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * reqId에 해당하는 ARTADVI 건이 있고, 현재 로그인 사용자가 해당 건의 멘토(ADV_ESNTL_ID)일 때만 해당 DTO 반환. 아니면 null.
     */
    private ArtadviDTO getMentorInfoIfMentor(String reqId) {
        if (reqId == null || reqId.trim().isEmpty()) return null;
        String currentEsntlId = getCurrentUniqId();
        if (currentEsntlId == null || currentEsntlId.trim().isEmpty()) return null;
        ArtadviListRequest advReq = new ArtadviListRequest();
        advReq.setReqId(reqId.trim());
        List<ArtadviDTO> advList = artadviManageService.selectArtadviList(advReq);
        if (advList == null || advList.isEmpty()) return null;
        boolean isMentor = advList.stream()
                .anyMatch(dto -> currentEsntlId.trim().equals(dto.getAdvEsntlId() != null ? dto.getAdvEsntlId().trim() : ""));
        if (!isMentor) return null;
        return advList.get(0);
    }

    @Operation(summary = "멘토정보 저장(사용자)", description = "멘토일지에서 상담장소·상담시간·상담내용·첨부파일(fileId) 수정. 해당 건의 배정 멘토만 호출 가능.")
    @PutMapping(value = "/mentor-info", produces = "application/json;charset=UTF-8", consumes = "application/json")
    public ResponseEntity<ArtappmResultResponse> saveMentorInfo(@RequestBody MentorInfoSaveRequest request) {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            if (request == null || !StringUtils.hasText(request.getReqId())) {
                response.setResult("40");
                response.setMessage("reqId가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            ArtadviDTO existing = getMentorInfoIfMentor(request.getReqId().trim());
            if (existing == null) {
                response.setResult("40");
                response.setMessage("해당 건에 대한 멘토 권한이 없거나 데이터가 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            ArtadviSaveRequest saveReq = new ArtadviSaveRequest();
            saveReq.setReqId(existing.getReqId());
            saveReq.setProId(existing.getProId());
            saveReq.setProSeq(existing.getProSeq());
            saveReq.setReqEsntlId(existing.getReqEsntlId());
            saveReq.setAdvEsntlId(existing.getAdvEsntlId());
            saveReq.setAdvDt(
                request.getAdvDt() != null && StringUtils.hasText(request.getAdvDt())
                    ? request.getAdvDt().trim()
                    : existing.getAdvDt()
            );
            saveReq.setAdvFrom(request.getAdvFrom() != null ? request.getAdvFrom() : existing.getAdvFrom());
            saveReq.setAdvTo(request.getAdvTo() != null ? request.getAdvTo() : existing.getAdvTo());
            saveReq.setAdvSpace(request.getAdvSpace() != null ? request.getAdvSpace() : existing.getAdvSpace());
            saveReq.setAdvDesc(request.getAdvDesc() != null ? request.getAdvDesc() : existing.getAdvDesc());
            if (request.getFileId() != null) {
                saveReq.setFileId(request.getFileId());
            } else if (existing.getFileId() != null && !existing.getFileId().isBlank()) {
                saveReq.setFileId(existing.getFileId());
            }
            saveReq.setChgUserId(getCurrentUniqId());
            artadviManageService.updateArtadvi(saveReq);
            if (!Boolean.TRUE.equals(request.getTempSave())) {
                artappmManageService.updateArtappmSttusCodeByReqId(request.getReqId().trim(), "04", null);
            }
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("saveMentorInfo: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "반려 저장(사용자)", description = "멘토일지에서 반려 사유(REA_DESC) 저장 및 STTUS_CODE=11 반려 처리. 이미 반려(11)인 경우 사유만 수정. 해당 건의 배정 멘토만 호출 가능.")
    @PutMapping(value = "/reject", produces = "application/json;charset=UTF-8", consumes = "application/json")
    public ResponseEntity<ArtappmResultResponse> saveReject(@RequestBody RejectSaveRequest request) {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            if (request == null || !StringUtils.hasText(request.getReqId())) {
                response.setResult("40");
                response.setMessage("reqId가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            ArtadviDTO existing = getMentorInfoIfMentor(request.getReqId().trim());
            if (existing == null) {
                response.setResult("40");
                response.setMessage("해당 건에 대한 멘토 권한이 없거나 데이터가 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            String reaDesc = request.getReaDesc() != null ? request.getReaDesc().trim() : "";
            artappmManageService.updateArtappmSttusCodeByReqId(request.getReqId().trim(), "11", reaDesc);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("saveReject: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "멘토정보 첨부파일 업로드(사용자)", description = "멘토일지에서 멘토정보 첨부파일 1건 추가. 해당 건의 배정 멘토만 호출 가능.")
    @PutMapping(value = "/mentor-info/upload", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappmResultResponse> uploadMentorInfoFile(
            @RequestParam String reqId,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            if (!StringUtils.hasText(reqId)) {
                response.setResult("40");
                response.setMessage("reqId가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            ArtadviDTO existing = getMentorInfoIfMentor(reqId.trim());
            if (existing == null) {
                response.setResult("40");
                response.setMessage("해당 건에 대한 멘토 권한이 없거나 데이터가 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (file == null || file.isEmpty()) {
                response.setResult("40");
                response.setMessage("파일이 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
            fileMap.put("file", file);
            FileDTO fileDTO = new FileDTO();
            String existingFileIdStr = existing.getFileId() != null && !existing.getFileId().isBlank() ? existing.getFileId().trim() : null;
            List<FileDTO> fileList;
            if (StringUtils.hasText(existingFileIdStr)) {
                int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(existingFileIdStr)) + 1;
                fileList = fileUtil.parseFileInf(fileMap, fileDTO, existingFileIdStr, "FILE_", nextSeq, "artappm", null);
            } else {
                fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "artappm", null);
            }
            if (fileList == null || fileList.isEmpty()) {
                response.setResult("01");
                response.setMessage("파일 저장에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            FileDTO info = fileList.get(0);
            info.setUNIQ_ID(getCurrentUniqId());
            fileManageService.insertFileInfo(info);
            String newFileId = String.valueOf(info.getFileId());
            ArtadviSaveRequest saveReq = new ArtadviSaveRequest();
            saveReq.setReqId(existing.getReqId());
            saveReq.setProId(existing.getProId());
            saveReq.setProSeq(existing.getProSeq());
            saveReq.setReqEsntlId(existing.getReqEsntlId());
            saveReq.setAdvEsntlId(existing.getAdvEsntlId());
            saveReq.setAdvDt(existing.getAdvDt());
            saveReq.setAdvFrom(existing.getAdvFrom());
            saveReq.setAdvTo(existing.getAdvTo());
            saveReq.setAdvSpace(existing.getAdvSpace());
            saveReq.setAdvDesc(existing.getAdvDesc());
            saveReq.setFileId(newFileId);
            saveReq.setChgUserId(getCurrentUniqId());
            artadviManageService.updateArtadvi(saveReq);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("uploadMentorInfoFile: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "멘토정보 첨부파일 1건 삭제(사용자)", description = "멘토일지에서 멘토정보 첨부파일 1건 삭제. 해당 건의 배정 멘토만 호출 가능. 남은 파일이 없으면 ARTADVI.FILE_ID 비움.")
    @DeleteMapping(value = "/mentor-info/files", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmResultResponse> deleteMentorInfoFile(
            @RequestParam String reqId,
            @RequestParam Long fileId,
            @RequestParam Integer seq) {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            if (!StringUtils.hasText(reqId)) {
                response.setResult("40");
                response.setMessage("reqId가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            ArtadviDTO existing = getMentorInfoIfMentor(reqId.trim());
            if (existing == null) {
                response.setResult("40");
                response.setMessage("해당 건에 대한 멘토 권한이 없거나 데이터가 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            String advFileId = existing.getFileId();
            if (advFileId == null || advFileId.isBlank()) {
                response.setResult("40");
                response.setMessage("첨부파일이 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (!advFileId.trim().equals(String.valueOf(fileId))) {
                response.setResult("40");
                response.setMessage("해당 신청 건의 첨부파일이 아닙니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            fileManageService.deleteFile(fileId, seq);
            List<FileDTO> remaining = fileManageService.selectFileListByFileId(fileId);
            if (remaining == null || remaining.isEmpty()) {
                ArtadviSaveRequest saveReq = new ArtadviSaveRequest();
                saveReq.setReqId(existing.getReqId());
                saveReq.setProId(existing.getProId());
                saveReq.setProSeq(existing.getProSeq());
                saveReq.setReqEsntlId(existing.getReqEsntlId());
                saveReq.setAdvEsntlId(existing.getAdvEsntlId());
                saveReq.setAdvDt(existing.getAdvDt());
                saveReq.setAdvFrom(existing.getAdvFrom());
                saveReq.setAdvTo(existing.getAdvTo());
                saveReq.setAdvSpace(existing.getAdvSpace());
                saveReq.setAdvDesc(existing.getAdvDesc());
                saveReq.setFileId("");
                saveReq.setChgUserId(getCurrentUniqId());
                artadviManageService.updateArtadvi(saveReq);
            }
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteMentorInfoFile: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** 멘토일지 진입 시 해당 신청 건의 상세(보호자·학생·학교 등) 조회. 현재 사용자가 해당 건의 배정 멘토(ADV_ESNTL_ID)일 때만 허용. */
    @Operation(summary = "멘토일지용 신청 상세 조회", description = "reqId로 지원사업 신청 상세 조회. 현재 로그인 사용자가 해당 건의 멘토(ARTADVI.ADV_ESNTL_ID)일 때만 반환.")
    @GetMapping(value = "/mentor-diary-detail", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getMentorDiaryDetail(@RequestParam String reqId) {
        try {
            if (reqId == null || reqId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            String currentEsntlId = getCurrentUniqId();
            if (currentEsntlId == null || currentEsntlId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            ArtadviListRequest advReq = new ArtadviListRequest();
            advReq.setReqId(reqId.trim());
            List<ArtadviDTO> advList = artadviManageService.selectArtadviList(advReq);
            if (advList == null || advList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            boolean isMentor = advList.stream()
                    .anyMatch(dto -> currentEsntlId.trim().equals(dto.getAdvEsntlId() != null ? dto.getAdvEsntlId().trim() : ""));
            if (!isMentor) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            ArtappmDetailRequest detailReq = new ArtappmDetailRequest();
            detailReq.setReqId(reqId.trim());
            ArtappmDTO detail = artappmManageService.selectArtappmDetail(detailReq);
            if (detail == null) {
                return ResponseEntity.ok(Map.of("detail", (Object) null, "result", "00"));
            }
            Map<String, Object> body = new java.util.LinkedHashMap<>();
            body.put("detail", detail);
            body.put("result", "00");
            if (detail.getFileId() != null && !detail.getFileId().trim().isEmpty()) {
                try {
                    Long fileId = Long.parseLong(detail.getFileId().trim());
                    List<FileDTO> fileDtoList = fileManageService.selectFileListByFileId(fileId);
                    List<Map<String, Object>> fileList = new ArrayList<>();
                    for (FileDTO f : fileDtoList) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("fileId", String.valueOf(f.getFileId()));
                        m.put("seq", f.getSeq());
                        m.put("orgfNm", f.getOrgfNm());
                        fileList.add(m);
                    }
                    body.put("fileList", fileList);
                } catch (NumberFormatException e) {
                    log.warn("getMentorDiaryDetail: invalid fileId, skip fileList. fileId={}", detail.getFileId());
                }
            }
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("getMentorDiaryDetail: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "멘토 신청 등록(사용자)", description = "multipart/form-data: data(JSON, proGb 필수 08|09·DB의 PRO_GB와 일치). REQ_ESNTL_ID는 서버가 로그인 멘토로 설정. mentorApplicationFiles(선택). 성공 시 result=00 및 reqId(ARTAPMM).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등록 성공(00) / 실패(01·40) / 중복(50)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "401", description = "미로그인"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(
            value = "/mentor-applications/{proId}",
            produces = "application/json;charset=UTF-8",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappmResultResponse> registerUserMentorApplication(
            @PathVariable String proId,
            @RequestPart("data") @Valid UserMentorApplicationRegisterRequest request,
            @RequestPart(value = "mentorApplicationFiles", required = false) List<MultipartFile> mentorApplicationFiles) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            String esntlId = getCurrentUniqId();
            if (!StringUtils.hasText(esntlId)) {
                response.setResult("40");
                response.setMessage("로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            request.setUniqId(esntlId);
            String fileUniqId = esntlId;

            Integer proSeqForDup = request.getProSeq() != null ? request.getProSeq() : 0;
            ArtapmmDuplicateCheckResponse dupPre = artappmManageService.checkMentorApplicationDuplicate(
                    proId, proSeqForDup, esntlId);
            if (dupPre.isDuplicate()) {
                ArtappmResultResponse dupRes = new ArtappmResultResponse();
                dupRes.setResult("50");
                dupRes.setMessage("동일 지원사업·회차에 이미 등록된 멘토 신청이 있습니다.");
                return ResponseEntity.ok(dupRes);
            }

            if (mentorApplicationFiles != null && !mentorApplicationFiles.isEmpty()) {
                Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                for (int i = 0; i < mentorApplicationFiles.size(); i++) {
                    MultipartFile f = mentorApplicationFiles.get(i);
                    String originalName = f != null ? f.getOriginalFilename() : null;
                    if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
                        fileMap.put("mentorApplicationFiles_" + i, f);
                    }
                }
                if (!fileMap.isEmpty()) {
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "artappm", request.getFileSeqs());
                    if (!fileList.isEmpty()) {
                        for (FileDTO fileInfo : fileList) {
                            fileInfo.setUNIQ_ID(fileUniqId);
                            fileManageService.insertFileInfo(fileInfo);
                        }
                        request.setFileId(String.valueOf(fileList.get(0).getFileId()));
                    }
                }
            }
            if (request.getFileId() == null) {
                request.setFileId("");
            }

            String chgUserId = esntlId;
            ArtappmResultResponse regResult = artappmManageService.registerMentorApplicationForUser(
                    proId, request, esntlId, getCurrentUserSe(), chgUserId);
            if (!"00".equals(regResult.getResult())) {
                return ResponseEntity.ok(regResult);
            }
            regResult.setMessage(egovMessageSource.getMessage("success.common.insert"));
            return ResponseEntity.ok(regResult);
        } catch (Exception e) {
            log.error("registerUserMentorApplication: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 신청 등록", description = "multipart/form-data: data(JSON, fileSeqs로 첨부파일별 seq 지정), artappmFiles(첨부파일 여러 개). 등록/수정 전 자격 조건(f_check) 검사 후, Y일 때만 저장.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등록/수정 성공(00) / 실패(01) / PK 중복(50) / 자격 미충족(02)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(
            value = {"", "/"},
            produces = "application/json;charset=UTF-8",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappmResultResponse> insertArtappm(
            @RequestPart("data") @Valid ArtappmInsertRequest request,
            @RequestPart(value = "proSeq", required = false) String proSeqPart,
            @RequestPart(value = "artappmFiles", required = false) List<MultipartFile> artappmFiles) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isEmpty()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            // 공고(새 신청) 정책: 사용자웹 POST는 INSERT only. 수정/임시저장 이어쓰기는 MY PAGE(reqId 기반)에서만 허용.
            if (request.getReqId() != null && !request.getReqId().isBlank()) {
                response.setResult("50");
                response.setMessage("수정은 MY PAGE에서만 가능합니다.");
                return ResponseEntity.ok(response);
            }
            // 03·05·07: PRO_SEQ 확실 반영 — JSON 바인딩 실패 시 별도 파트(proSeq)로 보정
            {
                String gb = request.getProGb() != null ? request.getProGb().trim() : "";
                if (("03".equals(gb) || "05".equals(gb) || "07".equals(gb))
                        && proSeqPart != null && !proSeqPart.isBlank()) {
                    String p = proSeqPart.trim();
                    if (!"0".equals(p)) {
                        request.setProSeq(p);
                    }
                }
            }
            String fileUniqId = request.getUNIQ_ID() != null ? request.getUNIQ_ID() : "";

            // 공고(새 신청) 정책: 기존 건이 있으면 즉시 50 반환 (슬롯 기준 중복 체크)
            if (request.getProId() != null && request.getReqEsntlId() != null
                    && !request.getProId().isBlank() && !request.getReqEsntlId().isBlank()
                    && artappmManageService.existsArtappmByPk(request)) {
                response.setResult("50");
                response.setMessage("동일한 지원사업 신청 건이 이미 존재합니다.");
                return ResponseEntity.ok(response);
            }

            // 신규 INSERT: sttusCode 기본값 01
            if (request.getSttusCode() == null || request.getSttusCode().isBlank()) {
                request.setSttusCode("01");
            }

            if (artappmFiles != null && !artappmFiles.isEmpty()) {
                Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                for (int i = 0; i < artappmFiles.size(); i++) {
                    MultipartFile f = artappmFiles.get(i);
                    String originalName = f != null ? f.getOriginalFilename() : null;
                    if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
                        fileMap.put("artappmFiles_" + i, f);
                    }
                }
                if (!fileMap.isEmpty()) {
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "artappm", request.getFileSeqs());
                    applyFileDescForProGb02(fileList, request.getProGb());
                    if (!fileList.isEmpty()) {
                        for (FileDTO fileInfo : fileList) {
                            fileInfo.setUNIQ_ID(fileUniqId);
                            fileManageService.insertFileInfo(fileInfo);
                        }
                        request.setFileId(String.valueOf(fileList.get(0).getFileId()));
                    }
                }
            }
            if (request.getFileId() == null) {
                request.setFileId("");
            }

            String proGb = request.getProGb() != null ? request.getProGb().trim() : "";
            // 공부의명수 신청 시 신청건 등록
            if ("08".equals(proGb) || "09".equals(proGb)) {
                if (!StringUtils.hasText(request.getReqId())) {
                    request.setReqId(artappmManageService.getNextReqId());
                }
                if (!StringUtils.hasText(request.getReqAppsId())) {
                    request.setReqAppsId(request.getReqId());
                }
                if (!StringUtils.hasText(request.getReqProSeq())) {
                    request.setReqProSeq(
                            StringUtils.hasText(request.getProSeq()) ? request.getProSeq().trim() : "0");
                }
                ArtappsInsertRequest artappsRequest = copyInsertToArtappsInsert(request);
                ArtappsResultResponse eligibilityFailure = artappsManageService.insertArtapps(artappsRequest);
                if (eligibilityFailure != null) {
                    response.setResult(eligibilityFailure.getResult());
                    response.setMessage(eligibilityFailure.getMessage());
                    return ResponseEntity.ok(response);
                }
            } else {
                ArtappmResultResponse eligibilityFailure = artappmManageService.insertArtappm(request);
                if (eligibilityFailure != null) {
                    return ResponseEntity.ok(eligibilityFailure);
                }
            }
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.insert"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("insertArtappm: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 신청 수정(reqId)", description = "MY PAGE 전용 수정 API. reqId 기준으로만 수정하며, 공고(새 신청)에서는 사용하지 않는다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공(00) / 실패(01) / 수정 불가(50) / 자격 미충족(02)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping(value = "/by-req-id/{reqId}", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappmResultResponse> updateArtappmByReqId(
            @PathVariable String reqId,
            @RequestPart("data") @Valid ArtappmInsertRequest request,
            @RequestPart(value = "proSeq", required = false) String proSeqPart,
            @RequestPart(value = "artappmFiles", required = false) List<MultipartFile> artappmFiles) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            if (!StringUtils.hasText(reqId)) {
                response.setResult("01");
                response.setMessage("필수 정보가 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isEmpty()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            // 03·05·07: PRO_SEQ 확실 반영 — JSON 바인딩 실패 시 별도 파트(proSeq)로 보정
            {
                String gb = request.getProGb() != null ? request.getProGb().trim() : "";
                if (("03".equals(gb) || "05".equals(gb) || "07".equals(gb))
                        && proSeqPart != null && !proSeqPart.isBlank()) {
                    String p = proSeqPart.trim();
                    if (!"0".equals(p)) {
                        request.setProSeq(p);
                    }
                }
            }
            String fileUniqId = request.getUNIQ_ID() != null ? request.getUNIQ_ID() : "";

            ArtappmDetailRequest byReqId = new ArtappmDetailRequest();
            byReqId.setReqId(reqId.trim());
            ArtappmDTO existing = artappmManageService.selectArtappmDetail(byReqId);
            if (existing == null) {
                response.setResult("01");
                response.setMessage("해당 지원사업 신청 건이 없습니다.");
                return ResponseEntity.ok(response);
            }
            // 권한: 본인(학생) 또는 보호자만 허용
            String currentEsntlId = getCurrentUniqId();
            String reqEsntl = existing.getReqEsntlId() != null ? existing.getReqEsntlId().trim() : "";
            String pEsntl = existing.getPEsntlId() != null ? existing.getPEsntlId().trim() : "";
            if (StringUtils.hasText(currentEsntlId)) {
                String cur = currentEsntlId.trim();
                if (!cur.equals(reqEsntl) && !cur.equals(pEsntl)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            boolean allowUpdate = "01".equals(existing.getSttusCode()) || "99".equals(request.getSttusCode());
            if (!allowUpdate) {
                response.setResult("50");
                response.setMessage("수정은 MY PAGE에서만 가능합니다.");
                return ResponseEntity.ok(response);
            }

            /** 05/07 다회차: 회차(PRO_SEQ) 변경 시 (PRO_ID, 대상 PRO_SEQ, 신청자) 슬롯에 이미 건이 있으면 중복(50), 저장 안 함 */
            {
                String proGbUpd = request.getProGb() != null ? request.getProGb().trim() : "";
                if ("05".equals(proGbUpd) || "07".equals(proGbUpd)) {
                    String existingSeq = existing.getProSeq() != null && !existing.getProSeq().trim().isEmpty()
                            ? existing.getProSeq().trim() : "0";
                    String reqSeqRaw = request.getProSeq() != null ? request.getProSeq().trim() : "";
                    String reqSeq = (reqSeqRaw.isEmpty() || "0".equals(reqSeqRaw)) ? existingSeq : reqSeqRaw;
                    if (!reqSeq.equals(existingSeq)) {
                        ArtappmInsertRequest dupCheck = new ArtappmInsertRequest();
                        dupCheck.setProId(StringUtils.hasText(existing.getProId()) ? existing.getProId().trim() : request.getProId());
                        dupCheck.setProGb(proGbUpd);
                        dupCheck.setProSeq(reqSeq);
                        if (StringUtils.hasText(existing.getCEsntlId())) {
                            dupCheck.setCEsntlId(existing.getCEsntlId().trim());
                        }
                        dupCheck.setReqEsntlId(existing.getReqEsntlId() != null ? existing.getReqEsntlId().trim() : "");
                        if (artappmManageService.existsArtappmByPk(dupCheck)) {
                            response.setResult("50");
                            response.setMessage("동일한 지원사업 신청 건이 이미 존재합니다.");
                            return ResponseEntity.ok(response);
                        }
                    }
                }
            }

            ArtappmUpdateRequest updateRequest = copyInsertToUpdate(request, existing.getProSeq(), request.getSttusCode());
            updateRequest.setReqId(existing.getReqId());
            /** MY PAGE 수정 시 JSON의 cEsntlId가 비어 있으면 기존 건 유지 (copy 누락·클라이언트 누락 시 C_ESNTL_ID NULL 방지) */
            if (!StringUtils.hasText(updateRequest.getCEsntlId())) {
                String existingC = existing.getCEsntlId() != null ? existing.getCEsntlId().trim() : "";
                if (StringUtils.hasText(existingC)) {
                    updateRequest.setCEsntlId(existingC);
                }
            }
            /** MY PAGE 수정: DB에 학생(C_ESNTL_ID)이 이미 있으면 다른 ID로 바꾸는 요청 거절 */
            {
                String existingC = existing.getCEsntlId() != null ? existing.getCEsntlId().trim() : "";
                String requestC = updateRequest.getCEsntlId() != null ? updateRequest.getCEsntlId().trim() : "";
                if (StringUtils.hasText(existingC) && StringUtils.hasText(requestC)
                        && !existingC.equals(requestC)) {
                    response.setResult("50");
                    response.setMessage("신청 건의 학생(자녀)은 변경할 수 없습니다.");
                    return ResponseEntity.ok(response);
                }
            }

            if (artappmFiles != null && !artappmFiles.isEmpty()) {
                Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                for (int i = 0; i < artappmFiles.size(); i++) {
                    MultipartFile f = artappmFiles.get(i);
                    String originalName = f != null ? f.getOriginalFilename() : null;
                    if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
                        fileMap.put("artappmFiles_" + i, f);
                    }
                }
                if (!fileMap.isEmpty()) {
                    String fileIdStr = updateRequest.getFileId() != null ? updateRequest.getFileId().trim() : "";
                    FileDTO fileDTO = new FileDTO();
                    if (fileIdStr.isEmpty()) {
                        List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "artappm", updateRequest.getFileSeqs());
                        applyFileDescForProGb02(fileList, updateRequest.getProGb());
                        if (!fileList.isEmpty()) {
                            for (FileDTO fileInfo : fileList) {
                                fileInfo.setUNIQ_ID(fileUniqId);
                                fileManageService.insertFileInfo(fileInfo);
                            }
                            updateRequest.setFileId(String.valueOf(fileList.get(0).getFileId()));
                        }
                    } else {
                        int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(fileIdStr)) + 1;
                        List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, fileIdStr, "FILE_", nextSeq, "artappm", updateRequest.getFileSeqs());
                        applyFileDescForProGb02(fileList, updateRequest.getProGb());
                        if (!fileList.isEmpty()) {
                            for (FileDTO fileInfo : fileList) {
                                fileInfo.setUNIQ_ID(fileUniqId);
                                fileManageService.insertFileInfo(fileInfo);
                            }
                        }
                    }
                }
            }
            if (updateRequest.getFileId() == null) {
                updateRequest.setFileId("");
            }
            String proGbUpdate = request.getProGb() != null ? request.getProGb().trim() : "";
            // 공부의명수 신청 시 신청건 수정
            if ("08".equals(proGbUpdate) || "09".equals(proGbUpdate)) {
                ArtappsUpdateRequest artappsUpdateRequest = copyInsertToArtappsUpdate(updateRequest);
                ArtappsResultResponse eligibilityFailure = artappsManageService.updateArtapps(artappsUpdateRequest);
                if (eligibilityFailure != null) {
                    response.setResult(eligibilityFailure.getResult());
                    response.setMessage(eligibilityFailure.getMessage());
                    return ResponseEntity.ok(response);
                }
            } else {
                ArtappmResultResponse eligibilityFailure = artappmManageService.updateArtappm(updateRequest);
                if (eligibilityFailure != null) {
                    return ResponseEntity.ok(eligibilityFailure);
                }
            }
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("updateArtappmByReqId: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** InsertRequest → UpdateRequest 복사. proSeq는 proGb=03·05·07일 때 요청 값(장소·시간 또는 회차), 그 외 기존 건 값. workDt는 03일 때 요청 값. */
    private static ArtappmUpdateRequest copyInsertToUpdate(ArtappmInsertRequest from, String existingProSeq, String requestSttusCode) {
        ArtappmUpdateRequest to = new ArtappmUpdateRequest();
        to.setProId(from.getProId());
        String gb = from.getProGb() != null ? from.getProGb().trim() : "";
        boolean useRequestProSeq = ("03".equals(gb) || "05".equals(gb) || "07".equals(gb))
                && from.getProSeq() != null && !from.getProSeq().isBlank() && !"0".equals(from.getProSeq().trim());
        String proSeq = useRequestProSeq
                ? from.getProSeq().trim()
                : (existingProSeq != null && !existingProSeq.isBlank() ? existingProSeq : "0");
        to.setProSeq(proSeq);
        to.setWorkDt("03".equals(gb) ? from.getWorkDt() : null);
        to.setReqEsntlId(from.getReqEsntlId());
        to.setCEsntlId(from.getCEsntlId());
        to.setProType(from.getProType());
        to.setPEsntlId(from.getPEsntlId());
        to.setHeadNm(from.getHeadNm());
        to.setPUserNm(from.getPUserNm());
        to.setMbtlnum(from.getMbtlnum());
        to.setBrthdy(from.getBrthdy());
        to.setPIhidnum(from.getPIhidnum());
        to.setCIhidnum(from.getCIhidnum());
        to.setCertYn(from.getCertYn());
        to.setCrtfcDnValue(from.getCrtfcDnValue());
        to.setSchoolId(from.getSchoolId());
        to.setSchoolGb(from.getSchoolGb());
        to.setSchoolNm(from.getSchoolNm());
        to.setSchoolLvl(from.getSchoolLvl());
        to.setSchoolNo(from.getSchoolNo());
        to.setPayBankCode(from.getPayBankCode());
        to.setPayBank(from.getPayBank());
        to.setHolderNm(from.getHolderNm());
        to.setReqPart(from.getReqPart());
        to.setPlayPart(from.getPlayPart());
        to.setReqObj(from.getReqObj());
        to.setReqPlay(from.getReqPlay());
        to.setReqPlan(from.getReqPlan());
        to.setMchilYn(from.getMchilYn());
        to.setMchilNm(from.getMchilNm());
        to.setReqDesc(from.getReqDesc());
        to.setFileId(from.getFileId());
        to.setFileSeqs(from.getFileSeqs());
        to.setResultGb(from.getResultGb());
        to.setReqDt(from.getReqDt());
        to.setAprrDt(from.getAprrDt());
        to.setChgDt(from.getChgDt());
        to.setStopDt(from.getStopDt());
        to.setReaDesc(from.getReaDesc());
        to.setSttusCode(requestSttusCode != null && !requestSttusCode.isBlank() ? requestSttusCode : "01");
        to.setUNIQ_ID(from.getUNIQ_ID());
        to.setProGb(from.getProGb());
        return to;
    }

    private static ArtappsInsertRequest copyInsertToArtappsInsert(ArtappmInsertRequest from) {
        ArtappsInsertRequest to = new ArtappsInsertRequest();
        if (from == null) {
            return to;
        }
        to.setReqId(from.getReqId());
        to.setReqProSeq(from.getReqProSeq());
        to.setProId(from.getProId());
        to.setProSeq(from.getProSeq());
        to.setReqEsntlId(from.getReqEsntlId());
        to.setCEsntlId(from.getCEsntlId());
        to.setProType(from.getProType());
        to.setPEsntlId(from.getPEsntlId());
        to.setHeadNm(from.getHeadNm());
        to.setPUserNm(from.getPUserNm());
        to.setMbtlnum(from.getMbtlnum());
        to.setBrthdy(from.getBrthdy());
        to.setPIhidnum(from.getPIhidnum());
        to.setCIhidnum(from.getCIhidnum());
        to.setCertYn(from.getCertYn());
        to.setCrtfcDnValue(from.getCrtfcDnValue());
        to.setSchoolId(from.getSchoolId());
        to.setSchoolGb(from.getSchoolGb());
        to.setSchoolNm(from.getSchoolNm());
        to.setSchoolLvl(from.getSchoolLvl());
        to.setSchoolNo(from.getSchoolNo());
        to.setPayBankCode(from.getPayBankCode());
        to.setPayBank(from.getPayBank());
        to.setHolderNm(from.getHolderNm());
        to.setReqPart(from.getReqPart());
        to.setPlayPart(from.getPlayPart());
        to.setReqObj(from.getReqObj());
        to.setReqPlay(from.getReqPlay());
        to.setReqPlan(from.getReqPlan());
        to.setMchilYn(from.getMchilYn());
        to.setMchilNm(from.getMchilNm());
        to.setReqDesc(from.getReqDesc());
        to.setFileId(from.getFileId());
        to.setResultGb(from.getResultGb());
        to.setReqDt(from.getReqDt());
        to.setWorkDt(from.getWorkDt());
        to.setAprrDt(from.getAprrDt());
        to.setChgDt(from.getChgDt());
        to.setStopDt(from.getStopDt());
        to.setReaDesc(from.getReaDesc());
        to.setSttusCode(from.getSttusCode());
        to.setUNIQ_ID(from.getUNIQ_ID());
        to.setProGb(from.getProGb());
        to.setReqAppsId(from.getReqAppsId());
        to.setProGbn(from.getProGbn());
        to.setProGbnEtc(from.getProGbnEtc());
        to.setReqSub(from.getReqSub());
        to.setJoinCnt(from.getJoinCnt());
        to.setBefJoin(from.getBefJoin());
        to.setJoinTime(from.getJoinTime());
        to.setJoinTimeCon(from.getJoinTimeCon());
        to.setSUnder(from.getSUnder());
        to.setSTarget(from.getSTarget());
        to.setSChar(from.getSChar());
        to.setSReason(from.getSReason());
        to.setSExpect(from.getSExpect());
        to.setSAppr(from.getSAppr());
        to.setSComm(from.getSComm());
        to.setAgree1Yn(from.getAgree1Yn());
        to.setAgree2Yn(from.getAgree2Yn());
        to.setAgree3Yn(from.getAgree3Yn());
        return to;
    }

    private static ArtappsUpdateRequest copyInsertToArtappsUpdate(ArtappmUpdateRequest from) {
        ArtappsUpdateRequest to = new ArtappsUpdateRequest();
        if (from == null) {
            return to;
        }
        to.setReqId(from.getReqId());
        to.setReqProSeq(from.getProSeq());
        to.setProId(from.getProId());
        to.setProSeq(from.getProSeq());
        to.setReqEsntlId(from.getReqEsntlId());
        to.setCEsntlId(from.getCEsntlId());
        to.setProType(from.getProType());
        to.setPEsntlId(from.getPEsntlId());
        to.setHeadNm(from.getHeadNm());
        to.setPUserNm(from.getPUserNm());
        to.setMbtlnum(from.getMbtlnum());
        to.setBrthdy(from.getBrthdy());
        to.setPIhidnum(from.getPIhidnum());
        to.setCIhidnum(from.getCIhidnum());
        to.setCertYn(from.getCertYn());
        to.setCrtfcDnValue(from.getCrtfcDnValue());
        to.setSchoolId(from.getSchoolId());
        to.setSchoolGb(from.getSchoolGb());
        to.setSchoolNm(from.getSchoolNm());
        to.setSchoolLvl(from.getSchoolLvl());
        to.setSchoolNo(from.getSchoolNo());
        to.setPayBankCode(from.getPayBankCode());
        to.setPayBank(from.getPayBank());
        to.setHolderNm(from.getHolderNm());
        to.setReqPart(from.getReqPart());
        to.setPlayPart(from.getPlayPart());
        to.setReqObj(from.getReqObj());
        to.setReqPlay(from.getReqPlay());
        to.setReqPlan(from.getReqPlan());
        to.setMchilYn(from.getMchilYn());
        to.setMchilNm(from.getMchilNm());
        to.setReqDesc(from.getReqDesc());
        to.setResultGb(from.getResultGb());
        to.setReqDt(from.getReqDt());
        to.setAprrDt(from.getAprrDt());
        to.setChgDt(from.getChgDt());
        to.setStopDt(from.getStopDt());
        to.setReaDesc(from.getReaDesc());
        to.setWorkDt(from.getWorkDt());
        to.setProGb(from.getProGb());
        to.setFileId(from.getFileId());
        to.setSttusCode(from.getSttusCode());
        to.setUNIQ_ID(from.getUNIQ_ID());
        to.setProGbn(from.getProGbn());
        to.setProGbnEtc(from.getProGbnEtc());
        to.setReqSub(from.getReqSub());
        to.setJoinCnt(from.getJoinCnt());
        to.setBefJoin(from.getBefJoin());
        to.setJoinTime(from.getJoinTime());
        to.setJoinTimeCon(from.getJoinTimeCon());
        to.setSUnder(from.getSUnder());
        to.setSTarget(from.getSTarget());
        to.setSChar(from.getSChar());
        to.setSReason(from.getSReason());
        to.setSExpect(from.getSExpect());
        to.setSAppr(from.getSAppr());
        to.setSComm(from.getSComm());
        to.setAgree1Yn(from.getAgree1Yn());
        to.setAgree2Yn(from.getAgree2Yn());
        to.setAgree3Yn(from.getAgree3Yn());
        return to;
    }

    @Operation(summary = "수강확인증 목록 조회(사용자)", description = "해당 신청 건의 수강확인증 전체 목록. 페이징 없음. searchProId, searchProSeq, searchReqEsntlId 필수.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/study-cert-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<StudyCertListResponse> selectStudyCertList(@RequestBody ArtappmListRequest request) throws Exception {
        StudyCertListResponse response = new StudyCertListResponse();
        try {
            List<StudyCertListItemResponse> data = artappmManageService.selectStudyCertListAll(request);
            int total = data != null ? data.size() : 0;
            response.setData(data);
            response.setRecordsFiltered(total);
            response.setRecordsTotal(total);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectStudyCertList: " + e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** 수강확인증 업로드 로직 (by-req-id에서 호출). */
    private ResponseEntity<ArtappmResultResponse> uploadStudyCert(
            @PathVariable String proId,
            @PathVariable String proSeq,
            @PathVariable String reqEsntlId,
            @RequestPart(value = "data", required = false) StudyCertUploadRequest data,
            @RequestPart(value = "studyCertFile", required = false) MultipartFile studyCertFile) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        String proSeqNorm = proSeq != null && !proSeq.isBlank() ? proSeq : "0";
        try {
            String fileDesc = data != null && data.getFileDesc() != null ? data.getFileDesc() : "";
            Date parsedUploadDttm = null;
            if (data != null && StringUtils.hasText(data.getUploadDttm())) {
                parsedUploadDttm = parseUploadDttm(data.getUploadDttm());
            }

            if (data != null && data.getSeq() != null) {
                int seq = data.getSeq();
                String existingFileId = artappmManageService.getStudyCertFileId(proId, proSeqNorm, reqEsntlId);
                if (existingFileId == null || existingFileId.isBlank()) {
                    response.setResult("01");
                    response.setMessage("수강확인증이 없습니다. 먼저 수강확인증을 등록하세요.");
                    return ResponseEntity.badRequest().body(response);
                }
                long fileId = Long.parseLong(existingFileId.trim());
                List<FileDTO> list = fileManageService.selectFileListByFileId(fileId);
                boolean seqExists = false;
                for (FileDTO f : list) {
                    if (f.getSeq() == seq) {
                        seqExists = true;
                        break;
                    }
                }
                if (!seqExists) {
                    response.setResult("01");
                    response.setMessage("해당 수강확인증(seq)이 없습니다.");
                    return ResponseEntity.badRequest().body(response);
                }
                if (studyCertFile != null && !studyCertFile.isEmpty()) {
                    fileManageService.updateFile(fileId, seq, studyCertFile, "FILE_", "artappm", fileDesc, parsedUploadDttm);
                } else {
                    fileManageService.updateFileMeta(fileId, seq, fileDesc, parsedUploadDttm);
                }
                response.setResult("00");
                response.setMessage(egovMessageSource.getMessage("success.common.update"));
                return ResponseEntity.ok(response);
            }

            if (studyCertFile == null || studyCertFile.isEmpty()) {
                response.setResult("01");
                response.setMessage("수강확인증 파일이 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
            fileMap.put("studyCertFile", studyCertFile);
            FileDTO fileDTO = new FileDTO();
            String existingFileId = artappmManageService.getStudyCertFileId(proId, proSeqNorm, reqEsntlId);
            List<FileDTO> fileList;
            if (StringUtils.hasText(existingFileId)) {
                int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(existingFileId.trim())) + 1;
                fileList = fileUtil.parseFileInf(fileMap, fileDTO, existingFileId.trim(), "FILE_", nextSeq, "artappm", null);
            } else {
                fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "artappm", null);
            }
            if (fileList.isEmpty()) {
                response.setResult("01");
                response.setMessage("파일 저장에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            FileDTO info = fileList.get(0);
            info.setFileDesc(fileDesc);
            if (parsedUploadDttm != null) {
                info.setUploadDttm(parsedUploadDttm);
            }
            info.setUNIQ_ID(getCurrentUniqId());
            fileManageService.insertFileInfo(info);
            String newFileId = String.valueOf(info.getFileId());
            artappmManageService.uploadStudyCert(proId, proSeqNorm, reqEsntlId, newFileId);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("uploadStudyCert: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** 수강확인증 1건 삭제 로직 (by-req-id에서 호출). */
    private ResponseEntity<ArtappmResultResponse> deleteStudyCert(
            @PathVariable String proId,
            @PathVariable String proSeq,
            @PathVariable String reqEsntlId,
            @RequestParam Integer seq) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            artappmManageService.deleteStudyCert(proId, proSeq, reqEsntlId, seq);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteStudyCert: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "변경이력 목록 조회", description = "해당 신청 건의 변경이력 목록. proId, proSeq, reqEsntlId 필수.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/{proId}/{proSeq}/{reqEsntlId}/change-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getChangeList(
            @PathVariable String proId,
            @PathVariable String proSeq,
            @PathVariable String reqEsntlId) throws Exception {
        Map<String, Object> body = new java.util.HashMap<>();
        try {
            String proSeqNorm = proSeq != null && !proSeq.isBlank() ? proSeq : "0";
            List<ChangeListItemResponse> data = artappmManageService.getChangeList(proId, proSeqNorm, reqEsntlId);
            body.put("result", "00");
            body.put("data", data != null ? data : new ArrayList<>());
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("getChangeList: " + e.getMessage(), e);
            body.put("result", "01");
            body.put("data", new ArrayList<>());
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    // ----- by-req-id (REQ_ID 단일 PK 기준, PRO_SEQ 변경에 안전) -----

    @Operation(summary = "첨부파일 1건 삭제(요청ID)", description = "reqId로 신청 건 식별. fileId+seq로 첨부파일 1건 삭제.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/by-req-id/{reqId}/files/{fileId}/{seq}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmResultResponse> deleteArtappmFileByReqId(
            @PathVariable String reqId,
            @PathVariable Long fileId,
            @PathVariable Integer seq) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            ArtappmDetailRequest detailReq = new ArtappmDetailRequest();
            detailReq.setReqId(reqId);
            ArtappmDTO detail = artappmManageService.selectArtappmDetail(detailReq);
            if (detail == null) {
                response.setResult("01");
                response.setMessage("해당 지원사업 신청 건이 없습니다.");
                return ResponseEntity.ok(response);
            }
            ArtappmFileDeleteRequest request = new ArtappmFileDeleteRequest();
            request.setReqId(detail.getReqId());
            request.setFileId(fileId);
            request.setSeq(seq);
            artappmManageService.deleteFile(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArtappmFileByReqId: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "수강확인증 상세 조회(요청ID)", description = "reqId로 신청 건 식별. query: seq(필수).")
    @GetMapping(value = "/by-req-id/{reqId}/study-cert", produces = "application/json;charset=UTF-8")
    public ResponseEntity<StudyCertDetailApiResponse> getStudyCertDetailByReqId(
            @PathVariable String reqId,
            @RequestParam Integer seq) throws Exception {
        StudyCertDetailApiResponse response = new StudyCertDetailApiResponse();
        try {
            StudyCertDetailRequest request = new StudyCertDetailRequest();
            request.setReqId(reqId);
            request.setSeq(seq);
            StudyCertDetailResponse detail = artappmManageService.getStudyCertDetail(request);
            response.setDetail(detail);
            response.setResult(detail != null ? "00" : "40");
            if (detail == null) {
                response.setMessage("등록된 수강확인증이 없습니다.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("getStudyCertDetailByReqId: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "수강확인증 업로드(요청ID)", description = "reqId로 신청 건 식별. data + studyCertFile multipart.")
    @PutMapping(value = "/by-req-id/{reqId}/study-cert", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappmResultResponse> uploadStudyCertByReqId(
            @PathVariable String reqId,
            @RequestPart(value = "data", required = false) StudyCertUploadRequest data,
            @RequestPart(value = "studyCertFile", required = false) MultipartFile studyCertFile) throws Exception {
        ArtappmDetailRequest detailReq = new ArtappmDetailRequest();
        detailReq.setReqId(reqId);
        ArtappmDTO detail = artappmManageService.selectArtappmDetail(detailReq);
        if (detail == null) {
            ArtappmResultResponse res = new ArtappmResultResponse();
            res.setResult("40");
            res.setMessage("해당 지원사업 신청 건이 없습니다.");
            return ResponseEntity.ok(res);
        }
        String proSeqNorm = detail.getProSeq() != null && !detail.getProSeq().isBlank() ? detail.getProSeq() : "0";
        return uploadStudyCert(detail.getProId(), proSeqNorm, detail.getReqEsntlId(), data, studyCertFile);
    }

    @Operation(summary = "수강확인증 1건 삭제(요청ID)", description = "reqId로 신청 건 식별. CORS 이슈 회피를 위해 POST 사용.")
    @PostMapping(value = "/by-req-id/{reqId}/study-cert/delete", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmResultResponse> deleteStudyCertByReqId(
            @PathVariable String reqId,
            @RequestParam Integer seq) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            artappmManageService.deleteStudyCertByReqId(reqId, seq);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteStudyCertByReqId: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "변경이력 목록 조회(요청ID)", description = "reqId로 신청 건 식별.")
    @GetMapping(value = "/by-req-id/{reqId}/change-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getChangeListByReqId(@PathVariable String reqId) throws Exception {
        ArtappmDetailRequest detailReq = new ArtappmDetailRequest();
        detailReq.setReqId(reqId);
        ArtappmDTO detail = artappmManageService.selectArtappmDetail(detailReq);
        Map<String, Object> body = new java.util.HashMap<>();
        if (detail == null) {
            body.put("result", "40");
            body.put("data", new ArrayList<>());
            return ResponseEntity.ok(body);
        }
        String proSeqNorm = detail.getProSeq() != null && !detail.getProSeq().isBlank() ? detail.getProSeq() : "0";
        List<ChangeListItemResponse> data = artappmManageService.getChangeList(detail.getProId(), proSeqNorm, detail.getReqEsntlId());
        body.put("result", "00");
        body.put("data", data != null ? data : new ArrayList<>());
        return ResponseEntity.ok(body);
    }
}
