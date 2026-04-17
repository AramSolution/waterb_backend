package arami.common.files.service.impl;

import arami.common.error.BusinessException;
import arami.common.error.ErrorCode;
import arami.common.files.FileUtil;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 파일 관리 서비스 구현체.
 * FileUtil(디스크 저장/썸네일/삭제)과 FileManageDAO(DB)를 조합하여
 * 파일 등록·조회·삭제·교체 비즈니스 로직을 처리.
 */
@Slf4j
@Service("fileManageService")
public class FileManageServiceImpl implements FileManageService {

    @Resource(name = "aramiFileManageDAO")
    private FileManageDAO fileManageDAO;

    @Resource(name = "fileUtil")
    private FileUtil fileUtil;

    @Override
    public int insertFileInfo(FileDTO fileDTO) throws Exception {
        return fileManageDAO.insertFileInfo(fileDTO);
    }

    @Override
    public int selectFileMaxSeq(Long fileId) throws Exception {
        return fileManageDAO.selectFileMaxSeq(fileId);
    }

    @Override
    public List<FileDTO> selectFileListByFileId(Long fileId) throws Exception {
        return fileManageDAO.selectFileListByFileId(fileId);
    }

    /** 단일 파일 삭제: DB 조회 → 디스크(원본+썸네일) 삭제 → DB 레코드 삭제 */
    @Override
    public void deleteFile(Long fileId, Integer seq) throws Exception {
        log.info("File delete start - fileId: {}, seq: {}", fileId, seq);

        List<FileDTO> fileList = fileManageDAO.selectFileListByFileId(fileId);
        FileDTO targetFile = null;

        for (FileDTO file : fileList) {
            if (file.getSeq() == seq) {
                targetFile = file;
                break;
            }
        }

        if (targetFile == null) {
            log.error("File not found - fileId: {}, seq: {}", fileId, seq);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        fileUtil.deleteFileWithThumbnails(targetFile);
        fileManageDAO.deleteFileRecord(targetFile);

        log.info("File delete done - fileId: {}, seq: {}", fileId, seq);
    }

    /** 파일 그룹 전체 삭제: 목록 조회 → 디스크 일괄 삭제 → DB 일괄 삭제 */
    @Override
    public Map<String, Object> deleteFileGroup(Long fileId) throws Exception {
        log.info("File group delete start - fileId: {}", fileId);

        List<FileDTO> fileList = fileManageDAO.selectFileListByFileId(fileId);

        if (fileList == null || fileList.isEmpty()) {
            log.error("File group not found - fileId: {}", fileId);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        fileUtil.deleteFileGroupWithThumbnails(fileList);
        int deletedCount = fileManageDAO.deleteFileGroupRecords(fileId);

        log.info("File group delete done - fileId: {}, deletedCount: {}", fileId, deletedCount);

        Map<String, Object> result = new HashMap<>();
        result.put("fileId", fileId);
        result.put("deletedCount", deletedCount);
        return result;
    }

    /** 파일 교체: 기존 정보 조회 → 새 파일 업로드(FileUtil) → DB UPDATE → 기존 파일 디스크 삭제 */
    @Override
    public FileDTO updateFile(Long fileId, Integer seq, MultipartFile newFile, String atchFileId, String storePath, String fileDesc, Date uploadDttm) throws Exception {
        log.info("File update start - fileId: {}, seq: {}", fileId, seq);

        List<FileDTO> fileList = fileManageDAO.selectFileListByFileId(fileId);
        FileDTO oldFile = null;

        for (FileDTO file : fileList) {
            if (file.getSeq() == seq) {
                oldFile = file;
                break;
            }
        }

        if (oldFile == null) {
            log.error("File not found - fileId: {}, seq: {}", fileId, seq);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        Map<String, MultipartFile> files = new HashMap<>();
        files.put(newFile.getName(), newFile);

        String storePathToUse = (storePath != null && !storePath.isBlank()) ? storePath : "test";
        FileDTO tempFileDTO = new FileDTO();
        List<FileDTO> uploadedFiles = fileUtil.parseFileInf(files, tempFileDTO, String.valueOf(fileId), atchFileId, seq, storePathToUse);

        if (uploadedFiles.isEmpty()) {
            log.error("File upload failed - fileId: {}, seq: {}", fileId, seq);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL);
        }

        FileDTO newFileInfo = uploadedFiles.get(0);

        FileDTO updateDTO = new FileDTO();
        updateDTO.setFileId(fileId);
        updateDTO.setSeq(seq);
        updateDTO.setSaveGb(newFileInfo.getSaveGb());
        updateDTO.setFilePath(newFileInfo.getFilePath());
        updateDTO.setOrgfNm(newFileInfo.getOrgfNm());
        updateDTO.setSaveNm(newFileInfo.getSaveNm());
        updateDTO.setFileExt(newFileInfo.getFileExt());
        updateDTO.setFileType(newFileInfo.getFileType());
        updateDTO.setFileSize(newFileInfo.getFileSize());
        updateDTO.setFileDesc(fileDesc);
        updateDTO.setUploadDttm(uploadDttm);

        fileManageDAO.updateFileForReplace(updateDTO);
        fileUtil.deleteFileWithThumbnails(oldFile);

        log.info("File update done - fileId: {}, seq: {}", fileId, seq);
        return updateDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reorderFilesInGroup(Long fileId, List<Integer> orderedCurrentSeqs) throws Exception {
        if (orderedCurrentSeqs == null || orderedCurrentSeqs.isEmpty()) {
            return;
        }
        List<FileDTO> files = fileManageDAO.selectFileListByFileId(fileId);
        if (files == null || files.isEmpty()) {
            log.warn("reorderFilesInGroup: no files for fileId={}", fileId);
            return;
        }
        if (files.size() != orderedCurrentSeqs.size()) {
            log.warn("reorderFilesInGroup: size mismatch fileId={} db={} order={}", fileId, files.size(),
                    orderedCurrentSeqs.size());
            return;
        }
        Set<Integer> present = new HashSet<>();
        for (FileDTO f : files) {
            present.add(f.getSeq());
        }
        for (int s : orderedCurrentSeqs) {
            if (!present.contains(s)) {
                log.warn("reorderFilesInGroup: unknown seq {} in order for fileId={}", s, fileId);
                return;
            }
        }
        if (new HashSet<>(orderedCurrentSeqs).size() != orderedCurrentSeqs.size()) {
            log.warn("reorderFilesInGroup: duplicate seq in order for fileId={}", fileId);
            return;
        }
        final int offset = 1_000_000;
        for (FileDTO f : files) {
            int n = fileManageDAO.updateFileSeq(fileId, f.getSeq(), f.getSeq() + offset);
            if (n != 1) {
                throw new IllegalStateException(
                        "reorderFilesInGroup bump: expected 1 row, got " + n + " fileId=" + fileId + " seq=" + f.getSeq());
            }
        }
        for (int i = 0; i < orderedCurrentSeqs.size(); i++) {
            int fromBumped = orderedCurrentSeqs.get(i) + offset;
            int n = fileManageDAO.updateFileSeq(fileId, fromBumped, i);
            if (n != 1) {
                throw new IllegalStateException("reorderFilesInGroup assign: expected 1 row, got " + n + " fileId="
                        + fileId + " fromBumped=" + fromBumped + " toSeq=" + i);
            }
        }
        log.info("reorderFilesInGroup: fileId={} new order {}", fileId, orderedCurrentSeqs);
    }

    @Override
    public void updateFileMeta(Long fileId, Integer seq, String fileDesc, Date uploadDttm) throws Exception {
        List<FileDTO> fileList = fileManageDAO.selectFileListByFileId(fileId);
        boolean found = false;
        for (FileDTO file : fileList) {
            if (file.getSeq() == seq) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        FileDTO param = new FileDTO();
        param.setFileId(fileId);
        param.setSeq(seq);
        param.setFileDesc(fileDesc);
        param.setUploadDttm(uploadDttm);
        fileManageDAO.updateFileMeta(param);
    }
}
