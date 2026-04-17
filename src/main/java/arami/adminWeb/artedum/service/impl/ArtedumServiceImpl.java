package arami.adminWeb.artedum.service.impl;

import arami.adminWeb.artedum.service.ArtedumManageDAO;
import arami.adminWeb.artedum.service.ArtedumService;
import arami.adminWeb.artedum.service.dto.request.ArtedumListRequest;
import arami.adminWeb.artedum.service.dto.response.ArtedumDTO;
import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 가맹학원(희망사업 신청) Service — 승인 목록 조회만 (사용자웹).
 */
@Service("artedumService")
public class ArtedumServiceImpl extends EgovAbstractServiceImpl implements ArtedumService {

    @Resource(name = "artedumManageDAO")
    private ArtedumManageDAO artedumManageDAO;

    @Override
    public List<ArtedumDTO> selectList(ArtedumListRequest request) {
        request.setDefaultPaging();
        return artedumManageDAO.selectList(request);
    }

    @Override
    public int selectListCount(ArtedumListRequest request) {
        request.setDefaultPaging();
        return artedumManageDAO.selectListCount(request);
    }
}
