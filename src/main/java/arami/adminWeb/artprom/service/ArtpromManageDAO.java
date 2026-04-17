package arami.adminWeb.artprom.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Repository;

import arami.adminWeb.artprom.service.dto.request.ArtpromDetailRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromInsertRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromListRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromUpdateRequest;
import arami.adminWeb.artprom.service.dto.request.ClearFileIdParam;
import arami.adminWeb.artprom.service.dto.response.ArtprodScheduleItemResponse;
import arami.adminWeb.artprom.service.dto.response.ArtpromDTO;

/**
 * 지원사업 관리 DAO
 */
@Repository("artpromManageDAO")
public class ArtpromManageDAO extends EgovAbstractMapper {

    public List<ArtpromDTO> selectArtpromList(ArtpromListRequest request) throws Exception {
        return selectList("artpromManageDAO.selectArtpromList", request);
    }

    public int selectArtpromListCount(ArtpromListRequest request) throws Exception {
        return selectOne("artpromManageDAO.selectArtpromListCount", request);
    }

    public List<ArtpromDTO> selectArtpromExcelList(ArtpromListRequest request) throws Exception {
        return selectList("artpromManageDAO.selectArtpromExcelList", request);
    }

    public ArtpromDTO selectArtpromDetail(ArtpromDetailRequest request) throws Exception {
        return selectOne("artpromManageDAO.selectArtpromDetail", request);
    }

    public String getNextProId() throws Exception {
        return selectOne("artpromManageDAO.getNextProId", new EgovMap());
    }

    public int insertArtprom(ArtpromInsertRequest request) throws Exception {
        return insert("artpromManageDAO.insertArtprom", request);
    }

    public int updateArtprom(ArtpromUpdateRequest request) throws Exception {
        return update("artpromManageDAO.updateArtprom", request);
    }

    public int deleteArtprom(ArtpromDetailRequest request) throws Exception {
        return update("artpromManageDAO.deleteArtprom", request);
    }

    public int clearProFileId(ClearFileIdParam param) throws Exception {
        return update("artpromManageDAO.clearProFileId", param);
    }

    public int clearFileId(ClearFileIdParam param) throws Exception {
        return update("artpromManageDAO.clearFileId", param);
    }

    public List<ArtprodScheduleItemResponse> selectArtprodListWithApplyCnt(ArtpromDetailRequest request) throws Exception {
        return selectList("artpromManageDAO.selectArtprodListWithApplyCnt", request);
    }
}
