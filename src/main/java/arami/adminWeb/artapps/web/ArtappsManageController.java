package arami.adminWeb.artapps.web;

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

import arami.common.CommonService;
import arami.common.files.FileUtil;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import arami.adminWeb.artapps.service.ArtappsManageService;
import arami.adminWeb.artapps.service.dto.request.ArtappsApplicationListRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsInsertRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsUpdateRequest;
import arami.adminWeb.artapps.service.dto.response.ArtappsApplicationListResponse;
import arami.adminWeb.artapps.service.dto.response.ArtappsApplicationListRowDTO;
import arami.adminWeb.artapps.service.dto.response.ArtappsResultResponse;
import arami.adminWeb.artprom.service.ArtpromManageService;
import arami.adminWeb.artprom.service.dto.request.ArtpromDetailRequest;
import arami.adminWeb.artprom.service.dto.response.ArtpromDTO;

@RestController
@RequestMapping("/api/admin/artapps")
@Slf4j
public class ArtappsManageController extends CommonService {

    private static final String FILE_PREFIX = "FILE_";
    /** ARTPROM/공부의명수 첨부 저장 경로 (기존 서비스와 동일) */
    private static final String FILE_STORE_PATH = "artprom";

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Resource
    private ArtappsManageService artappsManageService;

    @Resource(name = "fileUtil")
    private FileUtil fileUtil;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Resource
    private ArtpromManageService artpromManageService;

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

    /**
     * 지원사업 목록
     */
    @PostMapping(
            value = "/application-list",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappsApplicationListResponse> selectArtappsApplicationList(
            @RequestBody @Valid ArtappsApplicationListRequest request) {
        ArtappsApplicationListResponse response = new ArtappsApplicationListResponse();
        try {
            int totalCount = artappsManageService.countArtappsApplicationList(request);
            List<ArtappsApplicationListRowDTO> data =
                    artappsManageService.selectArtappsApplicationList(request);
            response.setData(data);
            response.setRecordsFiltered(totalCount);
            response.setRecordsTotal(totalCount);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectArtappsApplicationList: {}", e.getMessage(), e);
            response.setResult("01");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 공부의명수 신청 등록. multipart: data(JSON, fileSeqs로 첨부별 seq 지정), proSeq(선택), artappsFiles(첨부).
     * 구조는 {@link arami.adminWeb.artappm.web.ArtappmManageController#insertArtappm} 와 동일.
     */
    @PostMapping(
            value = "/",
            produces = "application/json;charset=UTF-8",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappsResultResponse> insertArtapps(
            @RequestPart("data") @Valid ArtappsInsertRequest request,
            @RequestPart(value = "proSeq", required = false) String proSeqPart,
            @RequestPart(value = "artappsFiles", required = false) List<MultipartFile> artappsFiles)
            throws Exception {
        ArtappsResultResponse response = new ArtappsResultResponse();
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

            if (artappsFiles != null && !artappsFiles.isEmpty()) {
                Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                for (int i = 0; i < artappsFiles.size(); i++) {
                    MultipartFile f = artappsFiles.get(i);
                    String originalName = f != null ? f.getOriginalFilename() : null;
                    if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
                        fileMap.put("artappsFiles_" + i, f);
                    }
                }
                if (!fileMap.isEmpty()) {
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList =
                            fileUtil.parseFileInf(
                                    fileMap,
                                    fileDTO,
                                    null,
                                    FILE_PREFIX,
                                    0,
                                    FILE_STORE_PATH,
                                    request.getFileSeqs());
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

            ArtappsResultResponse serviceResponse = artappsManageService.insertArtapps(request);
            return ResponseEntity.ok(serviceResponse);
        } catch (Exception e) {
            log.error("insertArtapps: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("공부의 명수 등록 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 공부의명수 신청 수정. multipart: data(JSON, fileSeqs), proSeq(선택), artappsFiles(추가 첨부).
     */
    @PutMapping(
            value = "/",
            produces = "application/json;charset=UTF-8",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtappsResultResponse> updateArtapps(
            @RequestPart("data") @Valid ArtappsUpdateRequest request,
            @RequestPart(value = "proSeq", required = false) String proSeqPart,
            @RequestPart(value = "artappsFiles", required = false) List<MultipartFile> artappsFiles)
            throws Exception {
        ArtappsResultResponse response = new ArtappsResultResponse();
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

            if (artappsFiles != null && !artappsFiles.isEmpty()) {
                Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                for (int i = 0; i < artappsFiles.size(); i++) {
                    MultipartFile f = artappsFiles.get(i);
                    String originalName = f != null ? f.getOriginalFilename() : null;
                    if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
                        fileMap.put("artappsFiles_" + i, f);
                    }
                }
                if (!fileMap.isEmpty()) {
                    String fileIdStr = request.getFileId() != null ? request.getFileId().trim() : "";
                    FileDTO fileDTO = new FileDTO();
                    if (fileIdStr.isEmpty()) {
                        List<FileDTO> fileList =
                                fileUtil.parseFileInf(
                                        fileMap,
                                        fileDTO,
                                        null,
                                        FILE_PREFIX,
                                        0,
                                        FILE_STORE_PATH,
                                        request.getFileSeqs());
                        if (!fileList.isEmpty()) {
                            for (FileDTO fileInfo : fileList) {
                                fileInfo.setUNIQ_ID(fileUniqId);
                                fileManageService.insertFileInfo(fileInfo);
                            }
                            request.setFileId(String.valueOf(fileList.get(0).getFileId()));
                        }
                    } else {
                        int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(fileIdStr)) + 1;
                        List<FileDTO> fileList =
                                fileUtil.parseFileInf(
                                        fileMap,
                                        fileDTO,
                                        fileIdStr,
                                        FILE_PREFIX,
                                        nextSeq,
                                        FILE_STORE_PATH,
                                        request.getFileSeqs());
                        for (FileDTO fileInfo : fileList) {
                            fileInfo.setUNIQ_ID(fileUniqId);
                            fileManageService.insertFileInfo(fileInfo);
                        }
                    }
                }
            }

            ArtappsResultResponse serviceResponse = artappsManageService.updateArtapps(request);
            return ResponseEntity.ok(serviceResponse);
        } catch (Exception e) {
            log.error("updateArtapps: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("공부의 명수 수정 중 오류가 발생했습니다.");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 지원사업신청ID(REQ_ID) 기준 지원사업 신청(ARTAPPM) 및 공부의 명수(ARTAPPS) 행을 삭제합니다.
     */
    @DeleteMapping(value = "/applications/by-req-id/{reqId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappsResultResponse> deleteApplicationsByReqId(
            @PathVariable("reqId") String reqId) {
        try {
            ArtappsResultResponse response =
                    artappsManageService.deleteApplicationsByReqId(reqId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteApplicationsByReqId reqId={}: {}", reqId, e.getMessage(), e);
            ArtappsResultResponse response = new ArtappsResultResponse();
            response.setResult("01");
            response.setMessage("공부의 명수 신청 데이터 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** REQ_ID 기준으로 ARTAPPM + ARTAPPS 상태 코드 동시 변경 */
    @PutMapping(value = "/applications/by-req-id/{reqId}/status-code", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtappsResultResponse> updateStatusCodeByReqId(
            @PathVariable("reqId") String reqId,
            @RequestParam("sttusCode") String sttusCode) {
        ArtappsResultResponse response = new ArtappsResultResponse();
        try {
            artappsManageService.updateArtappsSttusCodeByReqId(reqId, sttusCode, null);
            response.setResult("00");
            response.setMessage("상태코드가 변경되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setResult("40");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("updateStatusCodeByReqId reqId={}, sttusCode={}: {}", reqId, sttusCode, e.getMessage(), e);
            response.setResult("01");
            response.setMessage("상태코드 변경 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
