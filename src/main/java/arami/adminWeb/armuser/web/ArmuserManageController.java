package arami.adminWeb.armuser.web;

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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import arami.shared.armuser.dto.request.ArmuserDeleteRequest;
import arami.shared.armuser.dto.request.ArmuserDetailRequest;
import arami.shared.armuser.dto.request.ArmuserInsertRequest;
import arami.shared.armuser.dto.request.ArmuserListRequest;
import arami.shared.armuser.dto.request.ArmuserUpdateRequest;
import arami.shared.armuser.dto.response.ArmuserDTO;
import arami.shared.armuser.dto.response.ArmuserDetailResponse;
import arami.shared.armuser.dto.response.ArmuserExcelListResponse;
import arami.shared.armuser.dto.response.ArmuserListResponse;
import arami.shared.armuser.dto.response.ArmuserResultResponse;
import arami.common.CommonService;
import arami.shared.armuser.service.ArmuserService;
import arami.common.files.FileUtil;
import arami.common.files.service.FileManageService;
import arami.common.files.service.FileDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ARMUSER(통합회원) 관리 Controller (관리자웹)
 * 목록/상세/엑셀 조회 및 등록·수정·삭제 API.
 */
@Slf4j
@Tag(name = "통합회원 관리(관리자)", description = "관리자웹 - ARMUSER 등록/수정 API")
@RestController
@RequestMapping("/api/admin/armuser")
public class ArmuserManageController extends CommonService {

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Resource(name = "armuserService")
    private ArmuserService armuserService;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Resource(name = "fileUtil")
    private FileUtil fileUtil;

    @Operation(summary = "회원 목록 조회", description = "통합회원(ARMUSER) 목록 조회 (페이징/검색)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArmuserListResponse> list(@RequestBody ArmuserListRequest request) {
        ArmuserListResponse response = new ArmuserListResponse();
        int totalCount = armuserService.selectListCount(request);
        List<ArmuserDTO> data = armuserService.selectList(request);
        response.setData(data);
        response.setRecordsFiltered(totalCount);
        response.setRecordsTotal(totalCount);
        response.setResult("00");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 엑셀 목록 조회", description = "통합회원(ARMUSER) 엑셀용 목록 (페이징 없음)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/list/excel", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArmuserExcelListResponse> listExcel(@RequestBody ArmuserListRequest request) {
        ArmuserExcelListResponse response = new ArmuserExcelListResponse();
        try {
            response.setData(armuserService.selectExcelList(request));
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setResult("01");
            return ResponseEntity.ok(response);
        }
    }

    @Operation(summary = "회원 상세 조회", description = "통합회원(ARMUSER) 1건 상세 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/{esntlId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArmuserDetailResponse> getDetail(@PathVariable String esntlId) {
        ArmuserDetailRequest request = new ArmuserDetailRequest();
        request.setEsntlId(esntlId);
        ArmuserDTO detail = armuserService.selectDetail(request);
        ArmuserDetailResponse response = new ArmuserDetailResponse();
        response.setDetail(detail);
        if (detail != null && detail.getUserPic() != null && !detail.getUserPic().isBlank()) {
            try {
                Long fileId = Long.parseLong(detail.getUserPic().trim());
                List<FileDTO> fileList = fileManageService.selectFileListByFileId(fileId);
                response.setUserPicFiles(fileList != null ? fileList : new ArrayList<>());
            } catch (NumberFormatException e) {
                log.warn("getDetail: invalid userPic (fileId), skip userPicFiles. userPic={}", detail.getUserPic());
                response.setUserPicFiles(new ArrayList<>());
            } catch (Exception e) {
                log.warn("getDetail: selectFileListByFileId failed for userPic={}, {}", detail.getUserPic(), e.getMessage());
                response.setUserPicFiles(new ArrayList<>());
            }
        } else {
            response.setUserPicFiles(new ArrayList<>());
        }
        if (detail != null && detail.getAttaFile() != null && !detail.getAttaFile().isBlank()) {
            try {
                Long fileId = Long.parseLong(detail.getAttaFile().trim());
                List<FileDTO> fileList = fileManageService.selectFileListByFileId(fileId);
                response.setAttaFiles(fileList != null ? fileList : new ArrayList<>());
            } catch (NumberFormatException e) {
                log.warn("getDetail: invalid attaFile (fileId), skip attaFiles. attaFile={}", detail.getAttaFile());
                response.setAttaFiles(new ArrayList<>());
            } catch (Exception e) {
                log.warn("getDetail: selectFileListByFileId failed for attaFile={}, {}", detail.getAttaFile(), e.getMessage());
                response.setAttaFiles(new ArrayList<>());
            }
        } else {
            response.setAttaFiles(new ArrayList<>());
        }
        if (detail != null && detail.getBiznoFile() != null && !detail.getBiznoFile().isBlank()) {
            try {
                Long fileId = Long.parseLong(detail.getBiznoFile().trim());
                List<FileDTO> fileList = fileManageService.selectFileListByFileId(fileId);
                response.setBiznoFiles(fileList != null ? fileList : new ArrayList<>());
            } catch (NumberFormatException e) {
                log.warn("getDetail: invalid biznoFile (fileId), skip biznoFiles. biznoFile={}", detail.getBiznoFile());
                response.setBiznoFiles(new ArrayList<>());
            } catch (Exception e) {
                log.warn("getDetail: selectFileListByFileId failed for biznoFile={}, {}", detail.getBiznoFile(), e.getMessage());
                response.setBiznoFiles(new ArrayList<>());
            }
        } else {
            response.setBiznoFiles(new ArrayList<>());
        }
        response.setResult("00");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 등록", description = "통합회원(ARMUSER) 1건 등록. multipart: data(JSON), userPic(회원 사진, 선택), attachFiles(첨부파일 복수, 선택), bizCertFile(사업자등록증 1건, 선택). esntlId는 서버 채번. 아이디 중복 시 50 반환.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등록 성공(00) / 아이디 중복(50) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArmuserResultResponse> insertArmuser(
            @RequestPart("data") @Valid ArmuserInsertRequest request,
            @RequestPart(value = "userPic", required = false) MultipartFile userPicFile,
            @RequestPart(value = "attachFiles", required = false) List<MultipartFile> attachFiles,
            @RequestPart(value = "bizCertFile", required = false) MultipartFile bizCertFile) {
        ArmuserResultResponse response = new ArmuserResultResponse();
        try {
            String fileUniqId = getCurrentUniqId();
            // USER_PIC 파일 저장: 있으면 ARTFILE에 저장 후 request.userPic에 fileId 세팅
            String userPicFileName = userPicFile != null ? userPicFile.getOriginalFilename() : null;
            if (userPicFile != null && !userPicFile.isEmpty() && userPicFileName != null && !userPicFileName.isBlank()) {
                try {
                    Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                    fileMap.put("userPic_0", userPicFile);
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "armuser", null);
                    if (!fileList.isEmpty()) {
                        for (FileDTO fileInfo : fileList) {
                            fileInfo.setUNIQ_ID(fileUniqId);
                            fileManageService.insertFileInfo(fileInfo);
                        }
                        request.setUserPic(String.valueOf(fileList.get(0).getFileId()));
                    }
                } catch (Exception e) {
                    log.warn("insertArmuser: userPic file save failed, proceeding without userPic. {}", e.getMessage());
                }
            }
            // 첨부파일(ATTA_FILE): 복수 파일 저장 후 그룹당 fileId 1개만 request.attaFile에 세팅
            if (attachFiles != null && !attachFiles.isEmpty()) {
                try {
                    Map<String, MultipartFile> attachMap = new LinkedHashMap<>();
                    int i = 0;
                    for (MultipartFile f : attachFiles) {
                        String name = f != null ? f.getOriginalFilename() : null;
                        if (f != null && !f.isEmpty() && name != null && !name.isBlank()) {
                            attachMap.put("atta_" + i++, f);
                        }
                    }
                    if (!attachMap.isEmpty()) {
                        FileDTO fileDTO = new FileDTO();
                        List<FileDTO> fileList = fileUtil.parseFileInf(attachMap, fileDTO, null, "FILE_", 0, "armuser", null);
                        if (!fileList.isEmpty()) {
                            for (FileDTO fileInfo : fileList) {
                                fileInfo.setUNIQ_ID(fileUniqId);
                                fileManageService.insertFileInfo(fileInfo);
                            }
                            request.setAttaFile(String.valueOf(fileList.get(0).getFileId()));
                        }
                    }
                } catch (Exception e) {
                    log.warn("insertArmuser: attachFiles save failed, proceeding without attaFile. {}", e.getMessage());
                }
            }
            // 사업자등록증(BIZNO_FILE): 1건 저장 후 request.biznoFile에 fileId 세팅
            String bizCertFileName = bizCertFile != null ? bizCertFile.getOriginalFilename() : null;
            if (bizCertFile != null && !bizCertFile.isEmpty() && bizCertFileName != null && !bizCertFileName.isBlank()) {
                try {
                    Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                    fileMap.put("bizCert_0", bizCertFile);
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "armuser", null);
                    if (!fileList.isEmpty()) {
                        for (FileDTO fileInfo : fileList) {
                            fileInfo.setUNIQ_ID(fileUniqId);
                            fileManageService.insertFileInfo(fileInfo);
                        }
                        request.setBiznoFile(String.valueOf(fileList.get(0).getFileId()));
                    }
                } catch (Exception e) {
                    log.warn("insertArmuser: bizCertFile save failed, proceeding without biznoFile. {}", e.getMessage());
                }
            }
            ArmuserResultResponse failure = armuserService.insertArmuser(request);
            if (failure != null) {
                return ResponseEntity.ok(failure);
            }
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.insert"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("insertArmuser: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "회원 수정", description = "통합회원(ARMUSER) 1건 수정. multipart: data(JSON), userPic(회원 사진, 선택), attachFiles(첨부파일 복수, 선택), bizCertFile(사업자등록증 1건, 선택). esntlId는 path 변수.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping(value = "/{esntlId}", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArmuserResultResponse> updateArmuser(
            @PathVariable String esntlId,
            @RequestPart("data") @Valid ArmuserUpdateRequest request,
            @RequestPart(value = "userPic", required = false) MultipartFile userPicFile,
            @RequestPart(value = "attachFiles", required = false) List<MultipartFile> attachFiles,
            @RequestPart(value = "bizCertFile", required = false) MultipartFile bizCertFile) {
        ArmuserResultResponse response = new ArmuserResultResponse();
        try {
            request.setEsntlId(esntlId);
            String fileUniqId = getCurrentUniqId();
            ArmuserDetailRequest detailReq = new ArmuserDetailRequest();
            detailReq.setEsntlId(esntlId);
            ArmuserDTO current = armuserService.selectDetail(detailReq);

            // USER_PIC 파일 변경: 새 파일이 있으면 ARTFILE에 저장 후 request.userPic에 새 fileId 세팅
            String userPicFileName = userPicFile != null ? userPicFile.getOriginalFilename() : null;
            if (userPicFile != null && !userPicFile.isEmpty() && userPicFileName != null && !userPicFileName.isBlank()) {
                try {
                    Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                    fileMap.put("userPic_0", userPicFile);
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "armuser", null);
                    if (!fileList.isEmpty()) {
                        for (FileDTO fileInfo : fileList) {
                            fileInfo.setUNIQ_ID(fileUniqId);
                            fileManageService.insertFileInfo(fileInfo);
                        }
                        request.setUserPic(String.valueOf(fileList.get(0).getFileId()));
                    }
                } catch (Exception e) {
                    log.warn("updateArmuser: userPic file save failed, proceeding without userPic change. {}", e.getMessage());
                }
            } else {
                if (request.getUserPic() == null || request.getUserPic().trim().isEmpty()) {
                    if (current != null && current.getUserPic() != null && !current.getUserPic().trim().isEmpty()) {
                        request.setUserPic(current.getUserPic());
                    }
                }
            }

            // 첨부파일(ATTA_FILE): 새 파일이 있으면 기존 fileId에 추가 또는 신규 fileId 생성 (관리자 지원사업 신청과 동일)
            if (attachFiles != null && !attachFiles.isEmpty()) {
                try {
                    Map<String, MultipartFile> attachMap = new LinkedHashMap<>();
                    int i = 0;
                    for (MultipartFile f : attachFiles) {
                        String name = f != null ? f.getOriginalFilename() : null;
                        if (f != null && !f.isEmpty() && name != null && !name.isBlank()) {
                            attachMap.put("atta_" + i++, f);
                        }
                    }
                    if (!attachMap.isEmpty()) {
                        FileDTO fileDTO = new FileDTO();
                        String attaFileIdStr = current != null && current.getAttaFile() != null && !current.getAttaFile().trim().isEmpty()
                                ? current.getAttaFile().trim() : "";
                        List<FileDTO> fileList;
                        if (!attaFileIdStr.isEmpty()) {
                            try {
                                int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(attaFileIdStr)) + 1;
                                fileList = fileUtil.parseFileInf(attachMap, fileDTO, attaFileIdStr, "FILE_", nextSeq, "armuser", null);
                                if (!fileList.isEmpty()) {
                                    for (FileDTO fileInfo : fileList) {
                                        fileInfo.setUNIQ_ID(fileUniqId);
                                        fileManageService.insertFileInfo(fileInfo);
                                    }
                                }
                                request.setAttaFile(attaFileIdStr);
                            } catch (NumberFormatException e) {
                                log.warn("updateArmuser: invalid current attaFile (fileId), create new group. attaFile={}", attaFileIdStr);
                                fileList = fileUtil.parseFileInf(attachMap, fileDTO, null, "FILE_", 0, "armuser", null);
                                if (!fileList.isEmpty()) {
                                    for (FileDTO fileInfo : fileList) {
                                        fileInfo.setUNIQ_ID(fileUniqId);
                                        fileManageService.insertFileInfo(fileInfo);
                                    }
                                    request.setAttaFile(String.valueOf(fileList.get(0).getFileId()));
                                }
                            }
                        } else {
                            fileList = fileUtil.parseFileInf(attachMap, fileDTO, null, "FILE_", 0, "armuser", null);
                            if (!fileList.isEmpty()) {
                                for (FileDTO fileInfo : fileList) {
                                    fileInfo.setUNIQ_ID(fileUniqId);
                                    fileManageService.insertFileInfo(fileInfo);
                                }
                                request.setAttaFile(String.valueOf(fileList.get(0).getFileId()));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("updateArmuser: attachFiles save failed, proceeding without attaFile change. {}", e.getMessage());
                }
            } else {
                if (request.getAttaFile() == null || request.getAttaFile().trim().isEmpty()) {
                    if (current != null && current.getAttaFile() != null && !current.getAttaFile().trim().isEmpty()) {
                        request.setAttaFile(current.getAttaFile());
                    }
                }
            }

            // 사업자등록증(BIZNO_FILE): 새 파일이 있으면 저장 후 request.biznoFile에 fileId 세팅
            String bizCertFileName = bizCertFile != null ? bizCertFile.getOriginalFilename() : null;
            if (bizCertFile != null && !bizCertFile.isEmpty() && bizCertFileName != null && !bizCertFileName.isBlank()) {
                try {
                    Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
                    fileMap.put("bizCert_0", bizCertFile);
                    FileDTO fileDTO = new FileDTO();
                    List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "armuser", null);
                    if (!fileList.isEmpty()) {
                        for (FileDTO fileInfo : fileList) {
                            fileInfo.setUNIQ_ID(fileUniqId);
                            fileManageService.insertFileInfo(fileInfo);
                        }
                        request.setBiznoFile(String.valueOf(fileList.get(0).getFileId()));
                    }
                } catch (Exception e) {
                    log.warn("updateArmuser: bizCertFile save failed, proceeding without biznoFile change. {}", e.getMessage());
                }
            } else {
                if (request.getBiznoFile() == null || request.getBiznoFile().trim().isEmpty()) {
                    if (current != null && current.getBiznoFile() != null && !current.getBiznoFile().trim().isEmpty()) {
                        request.setBiznoFile(current.getBiznoFile());
                    }
                }
            }

            ArmuserResultResponse failure = armuserService.updateArmuser(request);
            if (failure != null) {
                return ResponseEntity.ok(failure);
            }
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("updateArmuser: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "USER_PIC(프로필 사진) 1건 삭제", description = "fileId+seq로 프로필 사진 1건 삭제. 해당 fileId에 남은 파일이 없으면 ARMUSER.USER_PIC를 비움.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패(@Valid)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{esntlId}/user-pic/{fileId}/{seq}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArmuserResultResponse> deleteArmuserUserPic(
            @PathVariable String esntlId,
            @PathVariable Long fileId,
            @PathVariable Integer seq) {
        ArmuserResultResponse response = new ArmuserResultResponse();
        try {
            armuserService.deleteUserPic(esntlId, fileId, seq);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArmuserUserPic: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "ATTA_FILE(첨부파일) 1건 삭제", description = "fileId+seq로 첨부파일 1건 삭제. 해당 fileId에 남은 파일이 없으면 ARMUSER.ATTA_FILE를 비움.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{esntlId}/atta-file/{fileId}/{seq}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArmuserResultResponse> deleteArmuserAttaFile(
            @PathVariable String esntlId,
            @PathVariable Long fileId,
            @PathVariable Integer seq) {
        ArmuserResultResponse response = new ArmuserResultResponse();
        try {
            armuserService.deleteAttaFile(esntlId, fileId, seq);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArmuserAttaFile: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "BIZNO_FILE(사업자등록증) 1건 삭제", description = "fileId+seq로 사업자등록증 1건 삭제 후 ARMUSER.BIZNO_FILE 비움.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{esntlId}/bizno-file/{fileId}/{seq}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArmuserResultResponse> deleteArmuserBiznoFile(
            @PathVariable String esntlId,
            @PathVariable Long fileId,
            @PathVariable Integer seq) {
        ArmuserResultResponse response = new ArmuserResultResponse();
        try {
            armuserService.deleteBiznoFile(esntlId, fileId, seq);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArmuserBiznoFile: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "회원 탈퇴(삭제)", description = "통합회원(ARMUSER) 탈퇴 처리. MBER_STTUS='D', SECSN_DE 설정.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "탈퇴 성공(00) / 실패(01)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping(value = "/{esntlId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArmuserResultResponse> deleteArmuser(@PathVariable String esntlId) {
        ArmuserResultResponse response = new ArmuserResultResponse();
        try {
            ArmuserDeleteRequest request = new ArmuserDeleteRequest();
            request.setEsntlId(esntlId);
            ArmuserResultResponse failure = armuserService.deleteArmuser(request);
            if (failure != null) {
                return ResponseEntity.ok(failure);
            }
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteArmuser: " + e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
