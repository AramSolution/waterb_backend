package arami.adminWeb.artadvi.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.adminWeb.artadvi.service.dto.request.ArtadviListRequest;
import arami.adminWeb.artadvi.service.dto.request.ArtadviSaveRequest;
import arami.adminWeb.artadvi.service.dto.response.ArtadviDTO;

/**
 * 상담관리(ARTADVI) DAO
 */
@Repository("artadviManageDAO")
public class ArtadviManageDAO extends EgovAbstractMapper {

    public List<ArtadviDTO> selectArtadviList(ArtadviListRequest request) {
        return selectList("artadviManageDAO.selectArtadviList", request);
    }

    public int insertArtadvi(ArtadviSaveRequest request) {
        return insert("artadviManageDAO.insertArtadvi", request);
    }

    public int updateArtadvi(ArtadviSaveRequest request) {
        return update("artadviManageDAO.updateArtadvi", request);
    }
}
