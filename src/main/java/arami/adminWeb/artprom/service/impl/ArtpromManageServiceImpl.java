package arami.adminWeb.artprom.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import arami.adminWeb.artprom.service.ArtpromManageDAO;
import arami.adminWeb.artprom.service.ArtpromManageService;
import arami.adminWeb.artprom.service.dto.request.ArtpromDetailRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromFileDeleteRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromInsertRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromListRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromUpdateRequest;
import arami.adminWeb.artprom.service.dto.request.ClearFileIdParam;
import arami.adminWeb.artprom.service.dto.response.ArtprodScheduleItemResponse;
import arami.adminWeb.artprom.service.dto.response.ArtpromDTO;
import arami.adminWeb.artprom.service.dto.response.ArtpromDetailResponse;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;

/**
 * 지원사업 관리 Service 구현
 */
@Service("artpromManageService")
public class ArtpromManageServiceImpl extends EgovAbstractServiceImpl implements ArtpromManageService {

    private static final Logger log = LoggerFactory.getLogger(ArtpromManageServiceImpl.class);

    @Resource(name = "artpromManageDAO")
    private ArtpromManageDAO artpromManageDAO;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Override
    public List<ArtpromDTO> selectArtpromList(ArtpromListRequest request) throws Exception {
        request.setDefaultPaging();
        return artpromManageDAO.selectArtpromList(request);
    }

    @Override
    public int selectArtpromListCount(ArtpromListRequest request) throws Exception {
        return artpromManageDAO.selectArtpromListCount(request);
    }

    @Override
    public List<ArtpromDTO> selectArtpromExcelList(ArtpromListRequest request) throws Exception {
        return artpromManageDAO.selectArtpromExcelList(request);
    }

    @Override
    public ArtpromDTO selectArtpromDetail(ArtpromDetailRequest request) throws Exception {
        return artpromManageDAO.selectArtpromDetail(request);
    }

    @Override
    public ArtpromDetailResponse selectArtpromDetailResponse(ArtpromDetailRequest request) throws Exception {
        ArtpromDTO detail = artpromManageDAO.selectArtpromDetail(request);
        ArtpromDetailResponse response = new ArtpromDetailResponse();
        response.setDetail(detail);

        List<Map<String, Object>> proFileList = new ArrayList<>();
        List<Map<String, Object>> fileList = new ArrayList<>();
        if (detail != null) {
            String proFileIdStr = detail.getProFileId();
            if (proFileIdStr != null && !proFileIdStr.trim().isEmpty()) {
                try {
                    Long proFileId = Long.parseLong(proFileIdStr.trim());
                    proFileList.addAll(toFileMapList(fileManageService.selectFileListByFileId(proFileId)));
                } catch (NumberFormatException e) {
                    log.warn("selectArtpromDetailResponse: invalid proFileId, skip proFileList. proFileId={}", proFileIdStr);
                }
            }
            String fileIdStr = detail.getFileId();
            if (fileIdStr != null && !fileIdStr.trim().isEmpty()) {
                try {
                    Long fileId = Long.parseLong(fileIdStr.trim());
                    fileList.addAll(toFileMapList(fileManageService.selectFileListByFileId(fileId)));
                } catch (NumberFormatException e) {
                    log.warn("selectArtpromDetailResponse: invalid fileId, skip fileList. fileId={}", fileIdStr);
                }
            }
        }
        response.setProFileList(proFileList);
        response.setFileList(fileList);
        return response;
    }

    private List<Map<String, Object>> toFileMapList(List<FileDTO> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (FileDTO f : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("fileId", String.valueOf(f.getFileId()));
            m.put("seq", String.valueOf(f.getSeq()));
            m.put("orgfNm", f.getOrgfNm());
            m.put("saveNm", f.getSaveNm());
            m.put("filePath", f.getFilePath());
            m.put("fileExt", f.getFileExt());
            m.put("fileSize", Float.valueOf(f.getFileSize()));
            m.put("fileType", f.getFileType());
            m.put("sttusCode", f.getSttusCode());
            result.add(m);
        }
        return result;
    }

    @Override
    public String getNextProId() throws Exception {
        return artpromManageDAO.getNextProId();
    }

    @Override
    public int insertArtprom(ArtpromInsertRequest request) throws Exception {
        return artpromManageDAO.insertArtprom(request);
    }

    @Override
    public int updateArtprom(ArtpromUpdateRequest request) throws Exception {
        return artpromManageDAO.updateArtprom(request);
    }

    @Override
    public int deleteArtprom(ArtpromDetailRequest request) throws Exception {
        return artpromManageDAO.deleteArtprom(request);
    }

    @Override
    public int clearProFileId(String proId) throws Exception {
        return artpromManageDAO.clearProFileId(new ClearFileIdParam(proId));
    }

    @Override
    public int clearFileId(String proId) throws Exception {
        return artpromManageDAO.clearFileId(new ClearFileIdParam(proId));
    }

    @Override
    public void deleteProFile(ArtpromFileDeleteRequest request) throws Exception {
        fileManageService.deleteFile(request.getFileId(), request.getSeq());
        List<FileDTO> remaining = fileManageService.selectFileListByFileId(request.getFileId());
        if (remaining.isEmpty() && request.getProId() != null && !request.getProId().trim().isEmpty()) {
            clearProFileId(request.getProId().trim());
        }
    }

    @Override
    public void deleteFile(ArtpromFileDeleteRequest request) throws Exception {
        fileManageService.deleteFile(request.getFileId(), request.getSeq());
        List<FileDTO> remaining = fileManageService.selectFileListByFileId(request.getFileId());
        if (remaining.isEmpty() && request.getProId() != null && !request.getProId().trim().isEmpty()) {
            clearFileId(request.getProId().trim());
        }
    }

    @Override
    public List<ArtprodScheduleItemResponse> getScheduleListWithApplyCnt(String proId) throws Exception {
        ArtpromDetailRequest request = new ArtpromDetailRequest();
        request.setProId(proId);
        return artpromManageDAO.selectArtprodListWithApplyCnt(request);
    }
}
