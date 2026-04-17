package arami.common.files.web;

import arami.common.CommonService;
import arami.common.error.BusinessException;
import arami.common.error.ErrorCode;
import arami.common.error.ResultResponse;
import arami.common.files.FileUtil;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import egovframework.com.cmm.EgovMessageSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 파일 전용 REST API 컨트롤러 (샘플/단독 업로드용).
 * 파일만 올리는 경우 사용. 게시글·첨부 등 도메인 연동 시에는 FileManageService를 주입해 한 번에 처리하는 것을 권장.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "FileManageController", description = "File upload API")
public class FileManageController extends CommonService {

    @Resource(name = "fileUtil")
    private FileUtil fileUtil;

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    /** 파일 조회 (이미지 표시·다운로드용). fileId+seq로 1건 조회 후 바이트 스트림 반환. */
    @Operation(summary = "File view", description = "Get file content by fileId and seq. Use as img src or download.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File content"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping(value = "/files/view", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> viewFile(
            @RequestParam(value = "fileId", required = true) Long fileId,
            @RequestParam(value = "seq", required = true) Integer seq,
            @RequestParam(value = "thumbSize", required = false) Integer thumbSize) throws Exception {
        LocalFileRef ref = resolveLocalFile(fileId, seq, thumbSize);
        if (ref == null) {
            return ResponseEntity.notFound().build();
        }
        InputStream in = new FileInputStream(ref.file);
        String contentType = ref.dto.getFileType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(ref.file.length());
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    /**
     * 첨부 다운로드 전용. Content-Disposition: attachment + UTF-8 파일명(RFC 5987 filename*).
     * 미리보기·img src는 {@link #viewFile} 사용.
     */
    @Operation(
            summary = "File download",
            description = "Download file by fileId and seq. Content-Disposition: attachment; filename from ORGF_NM."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File stream"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping(value = "/files/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam(value = "fileId", required = true) Long fileId,
            @RequestParam(value = "seq", required = true) Integer seq) throws Exception {
        LocalFileRef ref = resolveLocalFile(fileId, seq, null);
        if (ref == null) {
            return ResponseEntity.notFound().build();
        }
        InputStream in = new FileInputStream(ref.file);
        String contentType = ref.dto.getFileType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        String downloadName = ref.dto.getOrgfNm();
        if (downloadName == null || downloadName.isBlank()) {
            downloadName = ref.dto.getSaveNm() != null && !ref.dto.getSaveNm().isBlank()
                    ? ref.dto.getSaveNm()
                    : "download";
        }
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(downloadName, StandardCharsets.UTF_8)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(ref.file.length());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, disposition.toString());
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    /** FileUtil.createThumbnails 와 동일한 변형 크기만 허용 */
    private static boolean isAllowedThumbSize(int size) {
        return size == 32 || size == 64 || size == 128 || size == 256 || size == 500;
    }

    /**
     * 활성(STTUS_CODE=A만 목록 포함) 파일 메타 + 디스크 파일. 없으면 null.
     * thumbSize 지정 시 {@code filePath/thumb/{saveNm}_{thumbSize}.{ext}} 가 있으면 해당 파일 스트림(없으면 원본).
     */
    private LocalFileRef resolveLocalFile(Long fileId, Integer seq, Integer thumbSize) throws Exception {
        List<FileDTO> list = fileManageService.selectFileListByFileId(fileId);
        FileDTO target = null;
        for (FileDTO f : list) {
            if (f.getSeq() == seq) {
                target = f;
                break;
            }
        }
        if (target == null) {
            return null;
        }
        String pathStr = target.getFilePath();
        String saveNm = target.getSaveNm();
        if (pathStr == null || saveNm == null) {
            return null;
        }
        Path path = Paths.get(pathStr, saveNm);
        File file = path.toFile();
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        if (thumbSize != null && isAllowedThumbSize(thumbSize)) {
            String ext = target.getFileExt();
            if (ext != null && !ext.isBlank()) {
                String thumbName = saveNm + "_" + thumbSize + "." + ext;
                Path thumbPath = Paths.get(pathStr, "thumb", thumbName);
                File thumbFile = thumbPath.toFile();
                if (thumbFile.exists() && thumbFile.isFile()) {
                    return new LocalFileRef(target, thumbFile);
                }
            }
        }
        return new LocalFileRef(target, file);
    }

    private static final class LocalFileRef {
        final FileDTO dto;
        final File file;

        LocalFileRef(FileDTO dto, File file) {
            this.dto = dto;
            this.file = file;
        }
    }

    /** 다중 파일 업로드. fileId 없으면 자동 생성 후 디스크 저장 + DB insert */
    @Operation(
            summary = "File upload (multi)",
            description = "Upload multiple files. Max 100MB.",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload success", content = @Content(schema = @Schema(implementation = FileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/files/upload")
    public ResponseEntity<ResultResponse<FileDTO>> uploadMultipleFiles(
            MultipartHttpServletRequest request, ModelMap model,
            @RequestParam(value = "fileId", required = false) String fileId,
            @RequestParam(value = "seq", required = false, defaultValue = "FILE_") String seq) throws Exception {
        this.setCommon(request, model);

        FileDTO fileDTO = new FileDTO();

        try {
            Map<String, org.springframework.web.multipart.MultipartFile> files = request.getFileMap();
            List<FileDTO> fileList = fileUtil.parseFileInf(files, fileDTO, fileId, seq, 0, "test");

            if (!fileList.isEmpty()) {
                // 업로드된 파일 메타 정보를 DB에 저장
                for (FileDTO fileInfo : fileList) {
                    fileManageService.insertFileInfo(fileInfo);
                }
                // 에디터/프론트엔드에서 바로 사용할 수 있도록
                // 첫 번째 파일의 fileId, seq 등을 응답 데이터로 반환
                fileDTO = fileList.get(0);
            }

            return ResponseEntity.ok(
                    ResultResponse.success(
                            egovMessageSource.getMessage("file.upload.success"),
                            fileDTO
                    )
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("File upload error", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /** 기존 fileId 그룹에 파일 추가. nextSeq 계산 후 parseFileInf + insert */
    @Operation(
            summary = "Append files to existing file group",
            description = "Append files to existing fileId.",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Append success", content = @Content(schema = @Schema(implementation = FileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request (fileId or file missing)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "fileId not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/files/append")
    public ResponseEntity<ResultResponse<FileDTO>> appendFiles(
            MultipartHttpServletRequest request, ModelMap model,
            @RequestParam(value = "fileId", required = true) String fileId,
            @RequestParam(value = "seq", required = false, defaultValue = "FILE_") String seq) throws Exception {
        this.setCommon(request, model);
        FileDTO fileDTO = new FileDTO();

        try {
            Map<String, org.springframework.web.multipart.MultipartFile> files = request.getFileMap();

            if (files.isEmpty()) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }

            int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(fileId)) + 1;
            List<FileDTO> fileList = fileUtil.parseFileInf(files, fileDTO, fileId, seq, nextSeq, "test");

            if (!fileList.isEmpty()) {
                for (FileDTO fileInfo : fileList) { 
                    fileManageService.insertFileInfo(fileInfo);
                }
            }

            return ResponseEntity.ok(
                    ResultResponse.success(
                            "파일이 추가되었습니다.",
                            fileDTO
                    )
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("File append error - fileId: {}", fileId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /** 단일 파일 삭제(fileId+seq) 또는 그룹 전체 삭제(fileId만) */
    @Operation(
            summary = "Delete file(s)",
            description = "Delete one file (fileId + seq) or entire file group (fileId only).",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/files/delete")
    public ResponseEntity<ResultResponse<Void>> deleteFile(
            HttpServletRequest request, ModelMap model,
            @RequestParam(value = "fileId", required = true) Long fileId,
            @RequestParam(value = "seq", required = false) Integer seq) throws Exception {
        this.setCommon(request, model);

        try {
            if (seq != null) {
                fileManageService.deleteFile(fileId, seq);
            } else {
                fileManageService.deleteFileGroup(fileId);
            }

            return ResponseEntity.ok(
                    ResultResponse.success(
                            "파일이 삭제되었습니다.",
                            null
                    )
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("File delete error - fileId: {}, seq: {}", fileId, seq, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /** 기존 파일을 새 파일로 교체. 요청 파라미터에 key_fileId, key_seq 로 대상 지정 */
    @Operation(
            summary = "Update file (replace)",
            description = "Replace existing file by fileId and seq. Use fileId_seq as key for each file.",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update success", content = @Content(schema = @Schema(implementation = FileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/files/update")
    public ResponseEntity<ResultResponse<List<FileDTO>>> updateFiles(
            MultipartHttpServletRequest request, ModelMap model,
            @RequestParam(value = "atchFileId", required = false, defaultValue = "FILE_") String atchFileId) throws Exception {
        this.setCommon(request, model);

        try {
            Map<String, org.springframework.web.multipart.MultipartFile> files = request.getFileMap();

            if (files.isEmpty()) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }

            List<FileDTO> updatedFiles = new ArrayList<>();

            for (Map.Entry<String, org.springframework.web.multipart.MultipartFile> entry : files.entrySet()) {
                String key = entry.getKey();
                org.springframework.web.multipart.MultipartFile file = entry.getValue();

                String fileIdParam = request.getParameter(key + "_fileId");
                String seqParam = request.getParameter(key + "_seq");

                if (fileIdParam == null || seqParam == null) {
                    log.warn("fileId or seq param missing - key: {}", key);
                    continue;
                }

                Long fileId = Long.parseLong(fileIdParam);
                Integer seq = Integer.parseInt(seqParam);

                FileDTO updatedFile = fileManageService.updateFile(fileId, seq, file, atchFileId, null, null, null);
                updatedFiles.add(updatedFile);
            }

            return ResponseEntity.ok(
                    ResultResponse.success(
                            updatedFiles.size() + "개의 파일이 업데이트되었습니다.",
                            updatedFiles
                    )
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("File update error", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
