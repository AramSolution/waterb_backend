package arami.adminWeb.artadvi.service;

import java.util.List;

import arami.adminWeb.artadvi.service.dto.request.ArtadviListRequest;
import arami.adminWeb.artadvi.service.dto.request.ArtadviSaveRequest;
import arami.adminWeb.artadvi.service.dto.response.ArtadviDTO;

/**
 * 상담관리(ARTADVI) Service
 */
public interface ArtadviManageService {

    List<ArtadviDTO> selectArtadviList(ArtadviListRequest request);

    /**
     * 멘토지정 저장(upsert). REQ_ID로 ARTADVI 행이 없으면 INSERT, 있으면 UPDATE.
     * @return true면 INSERT, false면 UPDATE 수행됨
     */
    boolean saveArtadvi(ArtadviSaveRequest request);

    void insertArtadvi(ArtadviSaveRequest request);

    void updateArtadvi(ArtadviSaveRequest request);
}
