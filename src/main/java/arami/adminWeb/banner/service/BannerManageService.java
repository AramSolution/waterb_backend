package arami.adminWeb.banner.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import arami.adminWeb.banner.service.dto.request.BannerDetailRequest;
import arami.adminWeb.banner.service.dto.request.BannerListRequest;
import arami.adminWeb.banner.service.dto.request.BannerSaveRequest;
import arami.adminWeb.banner.service.dto.response.BannerDetailDataResponse;
import arami.adminWeb.banner.service.dto.response.BannerItemResponse;

public interface BannerManageService {
    List<BannerItemResponse> selectBannerList(BannerListRequest request) throws Exception;

    int selectBannerListCount(BannerListRequest request) throws Exception;

    BannerDetailDataResponse selectBannerDetail(BannerDetailRequest request) throws Exception;

    int insertBanner(BannerSaveRequest request, MultipartFile imageFile) throws Exception;

    int updateBanner(BannerSaveRequest request, MultipartFile imageFile) throws Exception;

    int deleteBannerImage(String banrCd, String uniqId) throws Exception;

    int deleteBanner(String banrCd, String uniqId) throws Exception;
}
