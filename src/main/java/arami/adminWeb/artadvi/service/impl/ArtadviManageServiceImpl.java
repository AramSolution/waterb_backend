package arami.adminWeb.artadvi.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import arami.adminWeb.artadvi.service.ArtadviManageDAO;
import arami.adminWeb.artadvi.service.ArtadviManageService;
import arami.adminWeb.artadvi.service.dto.request.ArtadviListRequest;
import arami.adminWeb.artadvi.service.dto.request.ArtadviSaveRequest;
import arami.adminWeb.artadvi.service.dto.response.ArtadviDTO;

/**
 * 상담관리(ARTADVI) Service 구현
 */
@Service("artadviManageService")
public class ArtadviManageServiceImpl implements ArtadviManageService {

    @Resource(name = "artadviManageDAO")
    private ArtadviManageDAO artadviManageDAO;

    @Override
    public List<ArtadviDTO> selectArtadviList(ArtadviListRequest request) {
        return artadviManageDAO.selectArtadviList(request);
    }

    @Override
    public boolean saveArtadvi(ArtadviSaveRequest request) {
        ArtadviListRequest listReq = new ArtadviListRequest();
        listReq.setReqId(request.getReqId());
        List<ArtadviDTO> existing = artadviManageDAO.selectArtadviList(listReq);
        if (existing == null || existing.isEmpty()) {
            artadviManageDAO.insertArtadvi(request);
            return true;
        }
        // 기존 멘토 그대로 저장 시: 요청에 fileId가 없으면(null) 화면에 뿌려진 기존 FILE_ID 유지. 멘토 변경 시 프론트가 '' 보내면 그대로 초기화.
        if (request.getFileId() == null && !existing.isEmpty()) {
            String existingFileId = existing.get(0).getFileId();
            if (existingFileId != null && !existingFileId.isBlank()) {
                request.setFileId(existingFileId);
            }
        }
        artadviManageDAO.updateArtadvi(request);
        return false;
    }

    @Override
    public void insertArtadvi(ArtadviSaveRequest request) {
        artadviManageDAO.insertArtadvi(request);
    }

    @Override
    public void updateArtadvi(ArtadviSaveRequest request) {
        artadviManageDAO.updateArtadvi(request);
    }
}
