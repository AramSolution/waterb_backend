package arami.adminWeb.banner.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.adminWeb.banner.service.dto.request.BannerDetailRequest;
import arami.adminWeb.banner.service.dto.request.BannerListRequest;
import arami.adminWeb.banner.service.dto.request.BannerSaveRequest;
import arami.adminWeb.banner.service.dto.response.BannerDetailDataResponse;
import arami.adminWeb.banner.service.dto.response.BannerItemResponse;

@Repository("bannerManageDAO")
public class BannerManageDAO extends EgovAbstractMapper {

    public List<BannerItemResponse> selectBannerList(BannerListRequest request) throws Exception {
        return selectList("bannerManageDAO.selectBannerList", request);
    }

    public int selectBannerListCount(BannerListRequest request) throws Exception {
        Integer count = selectOne("bannerManageDAO.selectBannerListCount", request);
        return count != null ? count : 0;
    }

    public BannerDetailDataResponse selectBannerDetail(BannerDetailRequest request) throws Exception {
        return selectOne("bannerManageDAO.selectBannerDetail", request);
    }

    public String createBanrCd() throws Exception {
        return selectOne("bannerManageDAO.createBanrCd");
    }

    public int insertBanner(BannerSaveRequest request) throws Exception {
        return insert("bannerManageDAO.insertBanner", request);
    }

    public int updateBanner(BannerSaveRequest request) throws Exception {
        return update("bannerManageDAO.updateBanner", request);
    }

    public int updateBannerImage(BannerSaveRequest request) throws Exception {
        return update("bannerManageDAO.updateBannerImage", request);
    }

    public int deleteBanner(BannerSaveRequest request) throws Exception {
        return update("bannerManageDAO.deleteBanner", request);
    }
}
