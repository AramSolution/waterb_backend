package arami.adminWeb.artappm.web;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.util.StringUtils;
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
import arami.adminWeb.artappm.service.ArtappmManageService;
import arami.adminWeb.artappm.service.dto.request.ArtappmDeleteRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmDetailRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmFileDeleteRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmInsertRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmSelectionBatchUpdateRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmSelectionListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmSelectionUpdateRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmUpdateRequest;
import arami.adminWeb.artappm.service.dto.request.StudyCertDetailRequest;
import arami.adminWeb.artappm.service.dto.request.StudyCertUploadRequest;
import arami.adminWeb.artappm.service.dto.response.ArtappmDTO;
import arami.adminWeb.artappm.service.dto.response.ArtappmDetailResponse;
import arami.adminWeb.artappm.service.dto.response.ArtappmExcelListResponse;
import arami.adminWeb.artappm.service.dto.response.ArtappmListResponse;
import arami.adminWeb.artappm.service.dto.response.ArtappmResultResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertDetailApiResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertDetailResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertExcelListResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertListItemResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertListResponse;
import arami.adminWeb.artprom.service.ArtpromManageService;
import arami.adminWeb.artprom.service.dto.request.ArtpromDetailRequest;
import arami.adminWeb.artprom.service.dto.response.ArtpromDTO;
import arami.shared.proc.service.ProcService;
import arami.shared.proc.dto.request.ChoiceListRequest;
import arami.shared.proc.dto.response.ChoiceListResponse;

/**
 * 지원사업 신청 관리 Controller (관리자웹)
 */
@Slf4j
@Tag(name = "지원사업 신청 관리", description = "관리자웹 - 지원사업 신청 관리 API")
@RestController
@RequestMapping("/api/admin/artappm")
public class ArtappmManageController extends CommonService {

    private static final Logger log = LoggerFactory.getLogger(ArtappmManageController.class);

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Resource
    private ArtappmManageService artappmManageService;

    @Resource(name = "fileUtil")
    private FileUtil fileUtil;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Resource(name = "procService")
    private ProcService procService;

    @Resource
    private ArtpromManageService artpromManageService;

    /** PRO_GB=02(사전지원)일 때 첨부파일 seq별 FILE_DESC */
    private static final Map<Integer, String> ARTAPPM_FILE_DESC_BY_SEQ = Map.of(
            1, "신청서",
            2, "개인정보수집 동의서",
            3, "신청자 점수 산정표",
            4, "신분증",
            5, "통장사본"
    );

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

    /**
     * proId 기준으로 ARTPROM.PRO_GB를 조회해 request.proGb를 보정한다.
     * 프론트에서 proGb 누락/오전송되어도 서버 규칙(특히 03 중복검사 분기)을 안정적으로 적용하기 위함.
     */
    private String normalizeProGb(String proId, String requestProGb) {
        if (!StringUtils.hasText(proId)) {
            return requestProGb;
        }
        try {
            ArtpromDetailRequest detailRequest = new ArtpromDetailRequest();
            detailRequest.setProId(proId.trim());
            ArtpromDTO program = artpromManageService.selectArtpromDetail(detailRequest);
            if (program != null && StringUtils.hasText(program.getProGb())) {
                return program.getProGb().trim();
            }
        } catch (Exception e) {
            log.warn("proGb 보정 실패: proId={}, requestProGb={}", proId, requestProGb, e);
        }
        return requestProGb;
    }

    @Operation(summary = "지원사업 신청 목록 조회", description = "페이징/검색 조건으로 지원사업 신청 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmListResponse> selectArtappmList(@RequestBody ArtappmListRequest request) throws Exception {
        ArtappmListResponse response = new ArtappmListResponse();
        try {
            int totalCount = artappmManageService.selectArtappmListCount(request);
            List<ArtappmDTO> data = artappmManageService.selectArtappmList(request);
            response.setData(data);
            response.setRecordsFiltered(totalCount);
            response.setRecordsTotal(totalCount);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectArtappmList: " + e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 신청 엑셀 목록 조회", description = "엑셀 다운로드용 지원사업 신청 목록을 조회합니다(검색 조건 적용, 페이징 없음).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/excel-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmExcelListResponse> selectArtappmExcelList(@RequestBody ArtappmListRequest request) throws Exception {
        ArtappmExcelListResponse response = new ArtappmExcelListResponse();
        try {
            List<ArtappmDTO> data = artappmManageService.selectArtappmExcelList(request);
            response.setData(data);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectArtappmExcelList: " + e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 신청 상세 조회(REQ_ID)", description = "지원사업신청ID(PK)로 한 건 상세 조회. 권장.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/by-req-id/{reqId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmDetailResponse> selectArtappmDetailByReqId(@PathVariable String reqId) throws Exception {
        ArtappmDetailResponse response = new ArtappmDetailResponse();
        try {
            ArtappmDetailRequest request = new ArtappmDetailRequest();
            request.setReqId(reqId);
            ArtappmDTO detail = artappmManageService.selectArtappmDetail(request);
            response.setDetail(detail);
            if (detail != null && detail.getFileId() != null && !detail.getFileId().isBlank()) {
                try {
                    Long fileId = Long.parseLong(detail.getFileId().trim());
                    List<FileDTO> fileList = fileManageService.selectFileListByFileId(fileId);
                    response.setFiles(fileList != null ? fileList : new ArrayList<>());
                } catch (NumberFormatException e) {
                    log.warn("selectArtappmDetailByReqId: invalid fileId={}", detail.getFileId());
                    response.setFiles(new ArrayList<>());
                }
            } else {
                response.setFiles(new ArrayList<>());
            }
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectArtappmDetailByReqId: " + e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 신청 등록", description = "multipart/form-data: data(JSON, fileSeqs로 첨부파일별 seq 지정), artappmFiles(첨부파일 여러 개). 등록 전 자격 조건(f_check) 검사 후, Y일 때만 등록.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등록 성공(00) / 실패(01) / PK 중복(50) / 자격 미충족(02)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappmResultResponse> insertArtappm(
            @RequestPart("data") @Valid ArtappmInsertRequest request,
            @RequestPart(value = "proSeq", required = false) String proSeqPart,
            @RequestPart(value = "artappmFiles", required = false) List<MultipartFile> artappmFiles) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            request.setProGb(normalizeProGb(request.getProId(), request.getProGb()));
            if ("03".equals(request.getProGb()) && StringUtils.hasText(proSeqPart)) {
                String p = proSeqPart.trim();
                if (!"0".equals(p)) {
                    request.setProSeq(p);
                }
            }
            // PK(지원사업·회차·신청자) 중복 체크: 먼저 검사하여 파일 저장 없이 즉시 반환
            if (request.getProId() != null && request.getReqEsntlId() != null
                    && !request.getProId().isBlank() && !request.getReqEsntlId().isBlank()) {
                if (artappmManageService.existsArtappmByPk(request)) {
                    response.setResult("50");
                    response.setMessage("동일한 지원사업 신청 건이 이미 존재합니다.");
                    return ResponseEntity.ok(response);
                }
            }
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isEmpty()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            String fileUniqId = request.getUNIQ_ID() != null ? request.getUNIQ_ID() : "";

            // 1) 첨부파일 저장 (seq는 data.fileSeqs 사용, 없으면 0,1,2 자동)
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

            // 2) 자격 조건 검사 후 DB 저장
            ArtappmResultResponse eligibilityFailure = artappmManageService.insertArtappm(request);
            if (eligibilityFailure != null) {
                return ResponseEntity.ok(eligibilityFailure);
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

    @Operation(summary = "지원사업 신청 수정", description = "multipart/form-data: data(JSON, fileSeqs로 첨부파일별 seq 지정), artappmFiles(첨부파일 추가). 수정 전 자격 조건(f_check) 검사 후, Y일 때만 수정.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공(00) / 실패(01) / 자격 미충족(02)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping(value = "/", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappmResultResponse> updateArtappm(
            @RequestPart("data") @Valid ArtappmUpdateRequest request,
            @RequestPart(value = "proSeq", required = false) String proSeqPart,
            @RequestPart(value = "artappmFiles", required = false) List<MultipartFile> artappmFiles) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            request.setProGb(normalizeProGb(request.getProId(), request.getProGb()));
            if ("03".equals(request.getProGb()) && StringUtils.hasText(proSeqPart)) {
                String p = proSeqPart.trim();
                if (!"0".equals(p)) {
                    request.setProSeq(p);
                }
            }
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isEmpty()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            String fileUniqId = request.getUNIQ_ID() != null ? request.getUNIQ_ID() : "";

            // 1) 첨부파일: 새 파일이 있으면 기존 fileId에 추가 또는 신규 fileId 생성
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
                    String fileIdStr = request.getFileId() != null ? request.getFileId().trim() : "";
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList;
                    if (fileIdStr.isEmpty()) {
                        fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "artappm", request.getFileSeqs());
                        applyFileDescForProGb02(fileList, request.getProGb());
                        if (!fileList.isEmpty()) {
                            for (FileDTO fileInfo : fileList) {
                                fileInfo.setUNIQ_ID(fileUniqId);
                                fileManageService.insertFileInfo(fileInfo);
                            }
                            request.setFileId(String.valueOf(fileList.get(0).getFileId()));
                        }
                    } else {
                        int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(fileIdStr)) + 1;
                        fileList = fileUtil.parseFileInf(fileMap, fileDTO, fileIdStr, "FILE_", nextSeq, "artappm", request.getFileSeqs());
                        applyFileDescForProGb02(fileList, request.getProGb());
                        if (!fileList.isEmpty()) {
                            for (FileDTO fileInfo : fileList) {
                                fileInfo.setUNIQ_ID(fileUniqId);
                                fileManageService.insertFileInfo(fileInfo);
                            }
                        }
                    }
                }
            }

            // 2) 자격 조건 검사 후 DB 수정
            ArtappmResultResponse eligibilityFailure = artappmManageService.updateArtappm(request);
            if (eligibilityFailure != null) {
                return ResponseEntity.ok(eligibilityFailure);
            }
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("updateArtappm: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "첨부파일 1건 삭제", description = "fileId+seq로 첨부파일 1건 삭제. 해당 fileId에 남은 파일이 없으면 ARTAPPM.FILE_ID를 비움.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{proId}/{proSeq}/{reqEsntlId}/files/{fileId}/{seq}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmResultResponse> deleteArtappmFile(
            @PathVariable String proId,
            @PathVariable String proSeq,
            @PathVariable String reqEsntlId,
            @PathVariable Long fileId,
            @PathVariable Integer seq) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            ArtappmDetailRequest detailReq = new ArtappmDetailRequest();
            detailReq.setProId(proId);
            detailReq.setProSeq(proSeq != null && !proSeq.isBlank() ? proSeq : "0");
            detailReq.setReqEsntlId(reqEsntlId);
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
            log.error("deleteArtappmFile: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "첨부파일 1건 삭제(reqId)", description = "REQ_ID 기준으로 첨부파일 1건 삭제. 해당 fileId에 남은 파일이 없으면 ARTAPPM.FILE_ID를 비움.")
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
            if (reqId == null || reqId.trim().isEmpty()) {
                response.setResult("01");
                response.setMessage("필수 정보가 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            ArtappmFileDeleteRequest request = new ArtappmFileDeleteRequest();
            request.setReqId(reqId.trim());
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

    @Operation(summary = "지원사업 신청 삭제", description = "지원사업 신청 한 건을 삭제합니다(REQ_ID 기준).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{reqId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmResultResponse> deleteArtappm(@PathVariable String reqId) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            ArtappmDeleteRequest request = new ArtappmDeleteRequest();
            request.setReqId(reqId);
            artappmManageService.deleteArtappm(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArtappm: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "수강확인증 목록 조회", description = "ARTAPPM.STUDY_CERT = ARTFILE.FILE_ID 조인. 지원사업당 여러 수강확인증(FILE_ID+SEQ) 가능, 한 행 = 수강확인증 파일 1건. rnum, proId, proSeq, reqEsntlId, fileId, seq, uploadDttm, fileDesc 반환.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/study-cert-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<StudyCertListResponse> selectStudyCertList(@RequestBody ArtappmListRequest request) throws Exception {
        StudyCertListResponse response = new StudyCertListResponse();
        try {
            int totalCount = artappmManageService.selectStudyCertListCount(request);
            List<StudyCertListItemResponse> data = artappmManageService.selectStudyCertList(request);
            response.setData(data);
            response.setRecordsFiltered(totalCount);
            response.setRecordsTotal(totalCount);
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

    @Operation(summary = "수강확인증 엑셀 목록 조회", description = "수강확인증 목록 엑셀용 (페이징 없음). searchProId, searchProSeq, searchReqEsntlId 조건만 적용.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/study-cert-list/excel", produces = "application/json;charset=UTF-8")
    public ResponseEntity<StudyCertExcelListResponse> selectStudyCertExcelList(@RequestBody ArtappmListRequest request) throws Exception {
        StudyCertExcelListResponse response = new StudyCertExcelListResponse();
        try {
            response.setData(artappmManageService.selectStudyCertExcelList(request));
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectStudyCertExcelList: " + e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "수강확인증 상세 조회(요청ID)", description = "reqId로 신청 건 식별. query: seq(필수).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공(00) / 수강확인증 없음(40)"),
        @ApiResponse(responseCode = "400", description = "seq 누락"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "수강확인증 업로드(요청ID)", description = "reqId로 신청 건 식별. data.seq 없음=추가, 있음=해당 SEQ 수정. multipart: data(JSON), studyCertFile(선택).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공(00) / 실패(01) / 해당 신청 건 없음(40)"),
        @ApiResponse(responseCode = "400", description = "파일 없음 / 수강확인증 없음 / 해당 seq 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping(value = "/by-req-id/{reqId}/study-cert", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappmResultResponse> uploadStudyCertByReqId(
            @PathVariable String reqId,
            @RequestPart(value = "data", required = false) StudyCertUploadRequest data,
            @RequestPart(value = "studyCertFile", required = false) MultipartFile studyCertFile) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            String fileDesc = data != null && data.getFileDesc() != null ? data.getFileDesc() : "";
            Date parsedUploadDttm = null;
            if (data != null && StringUtils.hasText(data.getUploadDttm())) {
                parsedUploadDttm = parseUploadDttm(data.getUploadDttm());
            }

            if (data != null && data.getSeq() != null) {
                int seq = data.getSeq();
                String existingFileId = artappmManageService.getStudyCertFileIdByReqId(reqId);
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
            String existingFileId = artappmManageService.getStudyCertFileIdByReqId(reqId);
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
            artappmManageService.uploadStudyCertByReqId(reqId, newFileId);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("uploadStudyCertByReqId: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "수강확인증 1건 삭제(요청ID)", description = "reqId로 신청 건 식별. seq로 수강확인증 파일 1건 삭제.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/by-req-id/{reqId}/study-cert", produces = "application/json;charset=UTF-8")
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
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "랜덤 신청자 선정 (f_choicelist)", description = "프로시저 f_choicelist를 호출하여 랜덤 신청자 목록을 조회합니다. aGubun(01: artappm), aProId, aProSeq, aDataCnt(선정 인원), aRank(선정 순위 옵션, 파이프 구분).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/choice-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<ChoiceListResponse>> getChoiceList(@RequestBody @Valid ChoiceListRequest request) {
        try {
            List<ChoiceListResponse> data = procService.getChoiceList(request);
            System.out.println("선정데이터터터터터터터 : " + data);
            System.out.println("choice-list 요청값 : aGubun=" + request.getAGubun()
                + ", aProId=" + request.getAProId()
                + ", aProSeq=" + request.getAProSeq()
                + ", aDataCnt=" + request.getADataCnt()
                + ", aRank=" + request.getARank());
            System.out.println("choice-list 응답 건수 : " + (data == null ? "null" : data.size()));
            if (data != null && !data.isEmpty()) {
                ChoiceListResponse first = data.get(0);
                System.out.println("choice-list 첫번째 응답값 : seqNo=" + first.getSeqNo()
                    + ", reqEsntlId=" + first.getCEsntlId());
            } else {
                System.out.println("choice-list 응답 상세 : 빈 배열 또는 null");
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("getChoiceList: " + e.getMessage(), e);
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "선정관리용 지원사업 신청 목록 조회", description = "PK(PRO_ID, PRO_SEQ, REQ_ESNTL_ID) 조건으로 지원사업 신청 목록을 조회합니다(페이징 없음).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/selection-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmExcelListResponse> selectArtappmSelectionList(@RequestBody @Valid ArtappmSelectionListRequest request) throws Exception {
        ArtappmExcelListResponse response = new ArtappmExcelListResponse();
        try {
            List<ArtappmDTO> data = artappmManageService.selectArtappmSelectionList(request);
            response.setData(data);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectArtappmSelectionList: " + e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "선정관리 선정여부 일괄 변경", description = "여러 신청 건의 선정여부(RESULT_GB)를 일괄 변경합니다. resultGb: Y(선정), N(미선정), R(예비).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "변경 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping(value = "/selection-update", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmResultResponse> updateArtappmSelectionBatch(@RequestBody @Valid ArtappmSelectionBatchUpdateRequest request) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            List<ArtappmSelectionUpdateRequest> list = request.getList() != null ? request.getList() : List.of();
            String chgUserId = getCurrentUniqId();
            artappmManageService.updateArtappmSelectionBatch(list, chgUserId);
            response.setResult("00");
            response.setMessage("선정여부가 저장되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("updateArtappmSelectionBatch: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage("선정여부 저장 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "상태 변경(요청ID)", description = "reqId로 신청 건 식별. sttusCode(02~12) 필수.")
    @PutMapping(value = "/by-req-id/{reqId}/status-code", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappmResultResponse> updateStatusCodeByReqId(
            @PathVariable String reqId,
            @RequestParam String sttusCode) throws Exception {
        ArtappmResultResponse response = new ArtappmResultResponse();
        try {
            artappmManageService.updateArtappmSttusCodeByReqId(reqId, sttusCode, null);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("updateStatusCodeByReqId: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
