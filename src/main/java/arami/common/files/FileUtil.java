package arami.common.files;

import arami.common.error.BusinessException;
import arami.common.error.ErrorCode;
import arami.common.files.service.FileDTO;
import arami.common.util.TsidUtil;
import egovframework.com.cmm.EgovWebUtil;
import egovframework.com.cmm.service.EgovProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 파일 디스크 저장·썸네일·삭제 유틸.
 * 확장자/용량 검증, 저장 경로 생성, 원본 저장, 이미지 썸네일 생성(32/64/128/256/500),
 * 원본·썸네일 삭제를 담당. DB 접근은 하지 않음.
 */
@Slf4j
@Component("fileUtil")
public class FileUtil {

    @Resource(name = "egovMessageSource")
    private egovframework.com.cmm.EgovMessageSource egovMessageSource;

    /**
     * multipart 파일 목록을 검증 후 디스크에 저장하고 FileDTO 목록 반환.
     * fileId 없으면 TSID로 새로 생성. 이미지면 썸네일 자동 생성.
     * seq는 clientSeqs가 있으면 해당 순서대로 사용, 없으면 fileKeyParam부터 0,1,2... 자동 부여.
     *
     * @param files multipart 파일 맵 (필드명 -> MultipartFile)
     * @param fileDTO 재사용용 DTO (내부에서 새로 생성해 채움)
     * @param fileId 파일 그룹 ID (null/공백이면 자동 생성)
     * @param seq 저장 파일명 prefix (예: "FILE_")
     * @param fileKeyParam 시작 순번 (clientSeqs 미사용 시 0 또는 append 시 nextSeq)
     * @param storePath 저장 하위 경로 (예: "test")
     * @return 디스크에 저장된 파일 정보 DTO 목록 (DB 저장은 호출부에서 insertFileInfo)
     */
    public List<FileDTO> parseFileInf(Map<String, MultipartFile> files, FileDTO fileDTO, String fileId, String seq, int fileKeyParam, String storePath) throws Exception {
        return parseFileInf(files, fileDTO, fileId, seq, fileKeyParam, storePath, null);
    }

    /**
     * multipart 파일 목록을 검증 후 디스크에 저장하고 FileDTO 목록 반환.
     * clientSeqs가 null/비어있으면 fileKeyParam부터 0,1,2... 자동 부여.
     * clientSeqs가 있으면 i번째 파일에 clientSeqs.get(i)를 seq로 사용 (고정 seq 지원, 예: 1=신청서, 2=동의서).
     *
     * @param files multipart 파일 맵 (필드명 -> MultipartFile)
     * @param fileDTO 재사용용 DTO (내부에서 새로 생성해 채움)
     * @param fileId 파일 그룹 ID (null/공백이면 자동 생성)
     * @param seq 저장 파일명 prefix (예: "FILE_")
     * @param fileKeyParam 시작 순번 (clientSeqs 미사용 시 사용)
     * @param storePath 저장 하위 경로 (예: "test")
     * @param clientSeqs 파일별 seq (null이면 자동 부여). 순서는 files 이터레이션 순서와 1:1
     * @return 디스크에 저장된 파일 정보 DTO 목록 (DB 저장은 호출부에서 insertFileInfo)
     */
    public List<FileDTO> parseFileInf(Map<String, MultipartFile> files, FileDTO fileDTO, String fileId, String seq, int fileKeyParam, String storePath, List<Integer> clientSeqs) throws Exception {
        List<FileDTO> result = new ArrayList<>();

        String allowedExtensions = EgovProperties.getProperty("Globals.fileUpload.Extensions") + EgovProperties.getProperty("Globals.fileUpload.Extensions.Images");
        for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
            MultipartFile file = entry.getValue();
            String originalFilename = file.getOriginalFilename();

            if (originalFilename != null && !originalFilename.isEmpty()) {
                String fileExtension = "";
                int lastIndex = originalFilename.lastIndexOf(".");
                if (lastIndex > 0) {
                    fileExtension = originalFilename.substring(lastIndex).toLowerCase();
                }

                if (!allowedExtensions.contains(fileExtension)) {
                    throw new BusinessException(ErrorCode.FILE_NOT_ALLOWED);
                }

                long maxFileSize = NumberUtils.createLong(EgovProperties.getProperty("Globals.posblAtchFileSize"));
                if (file.getSize() > maxFileSize) {
                    throw new BusinessException(ErrorCode.FILE_MAX_SIZE_EXCEEDED);
                }
            }
        }

        int fileKey = fileKeyParam;
        String storePathString = EgovProperties.getProperty("Globals.fileStorePath") + File.separator + storePath;
        String atchFileIdString = "";

        if (fileId != null) {
            fileId = fileId.replaceAll("\\s", "");
        }

        if ("".equals(fileId) || fileId == null) {
            atchFileIdString = String.valueOf(TsidUtil.generateLong());
        } else {
            atchFileIdString = fileId;
        }

        File saveFolder = new File(EgovWebUtil.filePathBlackList(storePathString));

        if (!saveFolder.exists() || saveFolder.isFile()) {
            saveFolder.mkdirs();
        }

        Iterator<Entry<String, MultipartFile>> itr = files.entrySet().iterator();
        MultipartFile file;
        String filePath = "";

        while (itr.hasNext()) {
            Entry<String, MultipartFile> entry = itr.next();
            file = entry.getValue();
            String orginFileName = file.getOriginalFilename();

            if (orginFileName == null) {
                orginFileName = "";
            }
            if ("".equals(orginFileName)) {
                continue;
            }

            int index = orginFileName.lastIndexOf(".");
            String fileExt = orginFileName.substring(index + 1);
            String newName = seq + TsidUtil.generateLong();
            long _size = file.getSize();

            if (!"".equals(orginFileName)) {
                String osName = System.getProperty("os.name").toLowerCase();
                filePath = storePathString + File.separator + newName;
                Path winFilePath = Paths.get(filePath).toAbsolutePath();

                if (osName.contains("win")) {
                    file.transferTo(new File(EgovWebUtil.filePathBlackList(winFilePath.toString())));
                } else {
                    file.transferTo(new File(EgovWebUtil.filePathBlackList(filePath)));
                }
            }

            if (isImageFile(fileExt)) {
                createThumbnails(filePath, storePathString, newName, fileExt);
            }

            int seqValue;
            if (clientSeqs != null && !clientSeqs.isEmpty() && result.size() < clientSeqs.size()) {
                seqValue = clientSeqs.get(result.size());
            } else {
                seqValue = fileKey;
                fileKey++;
            }

            fileDTO = new FileDTO();
            fileDTO.setSaveGb("L");
            fileDTO.setFileExt(fileExt);
            fileDTO.setFilePath(storePathString.replaceAll("\\\\", "/"));
            fileDTO.setFileSize(Float.parseFloat(Float.toString(_size)));
            fileDTO.setOrgfNm(orginFileName);
            fileDTO.setSaveNm(newName);
            fileDTO.setFileId(Long.valueOf(atchFileIdString));
            fileDTO.setSeq(seqValue);
            fileDTO.setFileType(file.getContentType());
            fileDTO.setTagNm("filename1");

            result.add(fileDTO);
        }

        return result;
    }

    /** 이미지 확장자 여부 (jpg, jpeg, png, gif, bmp, webp) */
    private boolean isImageFile(String fileExt) {
        String imageExtensions = "jpg,jpeg,png,gif,bmp,webp";
        return imageExtensions.contains(fileExt.toLowerCase());
    }

    /** 원본 비율 유지하여 32/64/128/256/500 px 썸네일 생성 (storePath/thumb/ 하위) */
    private void createThumbnails(String originalFilePath, String storePath, String fileName, String fileExt) {
        try {
            int[] thumbnailSizes = {32, 64, 128, 256, 500};
            String thumbDirPath = storePath + File.separator + "thumb";
            File thumbDir = new File(EgovWebUtil.filePathBlackList(thumbDirPath));
            if (!thumbDir.exists()) {
                thumbDir.mkdirs();
                log.info("Thumbnail dir created: {}", thumbDirPath);
            }

            File originalFile = new File(originalFilePath);
            if (!originalFile.exists()) {
                log.warn("Original file not found: {}", originalFilePath);
                return;
            }

            javax.imageio.ImageIO.setUseCache(false);
            java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(originalFile);

            if (originalImage == null) {
                log.warn("Cannot read image: {}", originalFilePath);
                return;
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            double aspectRatio = (double) originalWidth / originalHeight;

            for (int maxSize : thumbnailSizes) {
                if (originalWidth <= maxSize && originalHeight <= maxSize) {
                    continue;
                }

                int newWidth, newHeight;
                if (originalWidth > originalHeight) {
                    newWidth = Math.min(maxSize, originalWidth);
                    newHeight = (int) (newWidth / aspectRatio);
                } else {
                    newHeight = Math.min(maxSize, originalHeight);
                    newWidth = (int) (newHeight * aspectRatio);
                }

                java.awt.image.BufferedImage thumbnailImage = new java.awt.image.BufferedImage(
                        newWidth, newHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D graphics = thumbnailImage.createGraphics();
                graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                graphics.dispose();

                String thumbnailFileName = fileName + "_" + maxSize + "." + fileExt;
                String thumbnailFilePath = thumbDirPath + File.separator + thumbnailFileName;
                File thumbnailFile = new File(EgovWebUtil.filePathBlackList(thumbnailFilePath));
                String formatName = fileExt.equalsIgnoreCase("jpg") ? "jpeg" : fileExt;
                javax.imageio.ImageIO.write(thumbnailImage, formatName, thumbnailFile);
            }
        } catch (Exception e) {
            log.error("Thumbnail creation error: {}", e.getMessage(), e);
        }
    }

    /** 원본 파일 삭제. 이미지면 썸네일도 삭제. */
    public void deleteFileWithThumbnails(FileDTO fileDTO) throws Exception {
        try {
            String filePath = fileDTO.getFilePath();
            String saveNm = fileDTO.getSaveNm();
            String fileExt = fileDTO.getFileExt();

            String originalFilePath = filePath + File.separator + saveNm;
            File originalFile = new File(EgovWebUtil.filePathBlackList(originalFilePath));

            if (originalFile.exists()) {
                if (originalFile.delete()) {
                    log.info("Original file deleted: {}", originalFilePath);
                } else {
                    log.warn("Failed to delete original file: {}", originalFilePath);
                }
            }

            if (isImageFile(fileExt)) {
                deleteThumbnails(filePath, saveNm, fileExt);
            }
        } catch (Exception e) {
            log.error("File delete error", e);
            throw e;
        }
    }

    /** 썸네일 파일들만 삭제 (thumb 디렉토리 내 32/64/128/256/500 파일) */
    private void deleteThumbnails(String storePath, String fileName, String fileExt) {
        try {
            int[] thumbnailSizes = {32, 64, 128, 256, 500};
            String thumbDirPath = storePath + File.separator + "thumb";

            for (int size : thumbnailSizes) {
                String baseFileName = fileName;
                int lastDotIndex = fileName.lastIndexOf(".");
                if (lastDotIndex > 0) {
                    baseFileName = fileName.substring(0, lastDotIndex);
                }
                String thumbnailFileName = baseFileName + "_" + size + "." + fileExt;
                String thumbnailFilePath = thumbDirPath + File.separator + thumbnailFileName;
                File thumbnailFile = new File(EgovWebUtil.filePathBlackList(thumbnailFilePath));

                if (thumbnailFile.exists()) {
                    thumbnailFile.delete();
                }
            }
        } catch (Exception e) {
            log.error("Thumbnail delete error", e);
        }
    }

    /** 파일 그룹 전체 디스크 삭제 (원본+썸네일). DB 삭제는 서비스에서 별도 처리. */
    public void deleteFileGroupWithThumbnails(List<FileDTO> fileList) throws Exception {
        if (fileList == null || fileList.isEmpty()) {
            log.warn("File list to delete is empty.");
            return;
        }
        for (FileDTO fileDTO : fileList) {
            try {
                deleteFileWithThumbnails(fileDTO);
            } catch (Exception e) {
                log.error("File delete failed - fileId: {}, seq: {}", fileDTO.getFileId(), fileDTO.getSeq(), e);
            }
        }
    }
}
