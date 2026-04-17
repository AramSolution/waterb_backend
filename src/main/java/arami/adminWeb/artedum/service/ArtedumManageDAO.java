package arami.adminWeb.artedum.service;

import arami.adminWeb.artedum.service.dto.request.ArtedumListRequest;
import arami.adminWeb.artedum.service.dto.response.ArtedumDTO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 가맹학원(희망사업 신청) ARTEDUM — 목록 조회만 (사용자웹).
 */
@Repository("artedumManageDAO")
public class ArtedumManageDAO extends EgovAbstractMapper {

    public List<ArtedumDTO> selectList(ArtedumListRequest request) {
        return selectList("artedumManageDAO.selectList", request);
    }

    public int selectListCount(ArtedumListRequest request) {
        return selectOne("artedumManageDAO.selectListCount", request);
    }
}
