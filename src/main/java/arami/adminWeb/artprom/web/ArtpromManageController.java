package arami.adminWeb.artprom.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import arami.common.CommonService;
import arami.common.files.FileUtil;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import arami.adminWeb.artprom.service.ArtpromManageService;
import arami.adminWeb.artprom.service.dto.request.ArtpromDetailRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromFileDeleteRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromInsertRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromListRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromUpdateRequest;
import arami.adminWeb.artprom.service.dto.response.ArtpromDTO;
import arami.adminWeb.artprom.service.dto.response.ArtpromDetailResponse;
import arami.adminWeb.artprom.service.dto.response.ArtpromExcelListResponse;
import arami.adminWeb.artprom.service.dto.response.ArtpromListResponse;
import arami.adminWeb.artprom.service.dto.response.ArtpromResultResponse;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 지원사업 관리 Controller (관리자웹)
 */
@Slf4j
@Tag(name = "지원사업관리", description = "관리자웹 - 지원사업 관리 API")
@RestController
@RequestMapping("/api/admin/artprom")
public class ArtpromManageController extends CommonService {

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Resource
    private ArtpromManageService artpromManageService;

    @Resource(name = "fileUtil")
    private FileUtil fileUtil;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Operation(summary = "지원사업 목록 조회", description = "페이징/검색 조건으로 지원사업 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromListResponse> selectArtpromList(@RequestBody ArtpromListRequest request) throws Exception {
        ArtpromListResponse response = new ArtpromListResponse();
        try {
            int totalCount = artpromManageService.selectArtpromListCount(request);
            List<ArtpromDTO> data = artpromManageService.selectArtpromList(request);
            response.setData(data);
            response.setRecordsFiltered(totalCount);
            response.setRecordsTotal(totalCount);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectArtpromList: " + e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 엑셀 목록 조회", description = "엑셀 다운로드용 지원사업 목록을 조회합니다(검색 조건 적용, 페이징 없음).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/excel-list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromExcelListResponse> selectArtpromExcelList(@RequestBody ArtpromListRequest request) throws Exception {
        ArtpromExcelListResponse response = new ArtpromExcelListResponse();
        try {
            List<ArtpromDTO> data = artpromManageService.selectArtpromExcelList(request);
            response.setData(data);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectArtpromExcelList: " + e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 상세 조회", description = "상세 + 홍보파일(proFileList) + 첨부파일(fileList) 반환.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/{proId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromDetailResponse> selectArtpromDetail(@PathVariable String proId) throws Exception {
        try {
            ArtpromDetailRequest request = new ArtpromDetailRequest();
            request.setProId(proId);
            ArtpromDetailResponse response = artpromManageService.selectArtpromDetailResponse(request);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectArtpromDetail: " + e.getMessage(), e);
            ArtpromDetailResponse response = new ArtpromDetailResponse();
            response.setResult("01");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 등록", description = "multipart/form-data: data(JSON), proFile(홍보파일 1개), artpromFiles(첨부파일 여러 개).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등록 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromResultResponse> insertArtprom(
            @RequestPart("data") @Valid ArtpromInsertRequest request,
            @RequestPart(value = "proFile", required = false) MultipartFile proFile,
            @RequestPart(value = "artpromFiles", required = false) List<MultipartFile> artpromFiles) throws Exception {
        ArtpromResultResponse response = new ArtpromResultResponse();
        try {
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isEmpty()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            String fileUniqId = request.getUNIQ_ID() != null ? request.getUNIQ_ID() : "";

            // 1) 파일 저장(홍보파일 → PRO_FILE_ID, 첨부파일 → FILE_ID)
            String proFileName = proFile != null ? proFile.getOriginalFilename() : null;
            if (proFile != null && proFile.getSize() > 0 && proFileName != null && !proFileName.isEmpty()) {
                Map<String, MultipartFile> proFileMap = new LinkedHashMap<>();
                proFileMap.put("proFile", proFile);
                FileDTO fileDTO = new FileDTO();
                List<FileDTO> proFileList = fileUtil.parseFileInf(proFileMap, fileDTO, null, "FILE_", 0, "artprom");
                if (!proFileList.isEmpty()) {
                    for (FileDTO fileInfo : proFileList) {
                        fileInfo.setUNIQ_ID(fileUniqId);
                        fileManageService.insertFileInfo(fileInfo);
                    }
                    request.setProFileId(String.valueOf(proFileList.get(0).getFileId()));
                }
            }
            if (request.getProFileId() == null) {
                request.setProFileId("");
            }

            if (artpromFiles != null && !artpromFiles.isEmpty()) {
                Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                for (int i = 0; i < artpromFiles.size(); i++) {
                    MultipartFile f = artpromFiles.get(i);
                    String originalName = f != null ? f.getOriginalFilename() : null;
                    if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
                        fileMap.put("artpromFiles_" + i, f);
                    }
                }
                if (!fileMap.isEmpty()) {
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "artprom");
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

            // 2) DB 저장(트랜잭션은 Service에서 처리)
            String proId = artpromManageService.getNextProId();
            request.setProId(proId);
            artpromManageService.insertArtprom(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.insert"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("insertArtprom: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 수정", description = "multipart/form-data: data(JSON), proFile(홍보파일 1개 교체), artpromFiles(첨부파일 추가).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping(value = "/", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromResultResponse> updateArtprom(
            @RequestPart("data") @Valid ArtpromUpdateRequest request,
            @RequestPart(value = "proFile", required = false) MultipartFile proFile,
            @RequestPart(value = "artpromFiles", required = false) List<MultipartFile> artpromFiles) throws Exception {
        ArtpromResultResponse response = new ArtpromResultResponse();
        try {
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isEmpty()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            String fileUniqId = request.getUNIQ_ID() != null ? request.getUNIQ_ID() : "";

            // 1) 홍보파일: 새 파일이 있으면 새 proFileId로 교체
            String proFileName = proFile != null ? proFile.getOriginalFilename() : null;
            if (proFile != null && proFile.getSize() > 0 && proFileName != null && !proFileName.isEmpty()) {
                Map<String, MultipartFile> proFileMap = new LinkedHashMap<>();
                proFileMap.put("proFile", proFile);
                FileDTO fileDTO = new FileDTO();
                List<FileDTO> proFileList = fileUtil.parseFileInf(proFileMap, fileDTO, null, "FILE_", 0, "artprom");
                if (!proFileList.isEmpty()) {
                    for (FileDTO fileInfo : proFileList) {
                        fileInfo.setUNIQ_ID(fileUniqId);
                        fileManageService.insertFileInfo(fileInfo);
                    }
                    request.setProFileId(String.valueOf(proFileList.get(0).getFileId()));
                }
            }

            // 2) 첨부파일: 새 파일이 있으면 기존 fileId에 추가 또는 신규 fileId 생성 (Article과 동일)
            if (artpromFiles != null && !artpromFiles.isEmpty()) {
                Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                for (int i = 0; i < artpromFiles.size(); i++) {
                    MultipartFile f = artpromFiles.get(i);
                    String originalName = f != null ? f.getOriginalFilename() : null;
                    if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
                        fileMap.put("artpromFiles_" + i, f);
                    }
                }
                if (!fileMap.isEmpty()) {
                    String fileIdStr = request.getFileId() != null ? request.getFileId().trim() : "";
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList;
                    if (fileIdStr.isEmpty()) {
                        fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "artprom");
                        if (!fileList.isEmpty()) {
                            for (FileDTO fileInfo : fileList) {
                                fileInfo.setUNIQ_ID(fileUniqId);
                                fileManageService.insertFileInfo(fileInfo);
                            }
                            request.setFileId(String.valueOf(fileList.get(0).getFileId()));
                        }
                    } else {
                        int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(fileIdStr)) + 1;
                        fileList = fileUtil.parseFileInf(fileMap, fileDTO, fileIdStr, "FILE_", nextSeq, "artprom");
                        if (!fileList.isEmpty()) {
                            for (FileDTO fileInfo : fileList) {
                                fileInfo.setUNIQ_ID(fileUniqId);
                                fileManageService.insertFileInfo(fileInfo);
                            }
                        }
                    }
                }
            }

            // 3) DB 수정(트랜잭션은 Service에서 처리)
            artpromManageService.updateArtprom(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("updateArtprom: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "홍보파일 1건 삭제", description = "fileId+seq로 홍보파일 1건 삭제. 해당 fileId에 남은 파일이 없으면 ARTPROM.PRO_FILE_ID를 비움.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{proId}/pro-files/{fileId}/{seq}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromResultResponse> deleteArtpromProFile(
            @PathVariable String proId,
            @PathVariable Long fileId,
            @PathVariable Integer seq) throws Exception {
        ArtpromResultResponse response = new ArtpromResultResponse();
        try {
            ArtpromFileDeleteRequest request = new ArtpromFileDeleteRequest();
            request.setProId(proId);
            request.setFileId(fileId);
            request.setSeq(seq);
            artpromManageService.deleteProFile(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArtpromProFile: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "첨부파일 1건 삭제", description = "fileId+seq로 첨부파일 1건 삭제. 해당 fileId에 남은 파일이 없으면 ARTPROM.FILE_ID를 비움.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{proId}/files/{fileId}/{seq}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromResultResponse> deleteArtpromFile(
            @PathVariable String proId,
            @PathVariable Long fileId,
            @PathVariable Integer seq) throws Exception {
        ArtpromResultResponse response = new ArtpromResultResponse();
        try {
            ArtpromFileDeleteRequest request = new ArtpromFileDeleteRequest();
            request.setProId(proId);
            request.setFileId(fileId);
            request.setSeq(seq);
            artpromManageService.deleteFile(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArtpromFile: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "지원사업 삭제", description = "지원사업을 삭제합니다(소프트 삭제: STTUS_CODE=99).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{proId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtpromResultResponse> deleteArtprom(@PathVariable String proId) throws Exception {
        ArtpromResultResponse response = new ArtpromResultResponse();
        try {
            ArtpromDetailRequest request = new ArtpromDetailRequest();
            request.setProId(proId);
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isEmpty()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            artpromManageService.deleteArtprom(request);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArtprom: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
