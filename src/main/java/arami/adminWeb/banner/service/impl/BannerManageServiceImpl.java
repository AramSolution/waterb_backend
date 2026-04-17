package arami.adminWeb.banner.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import arami.adminWeb.banner.service.BannerManageDAO;
import arami.adminWeb.banner.service.BannerManageService;
import arami.adminWeb.banner.service.dto.request.BannerDetailRequest;
import arami.adminWeb.banner.service.dto.request.BannerListRequest;
import arami.adminWeb.banner.service.dto.request.BannerSaveRequest;
import arami.adminWeb.banner.service.dto.response.BannerDetailDataResponse;
import arami.adminWeb.banner.service.dto.response.BannerItemResponse;
import arami.common.files.FileUtil;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;

@Service("bannerManageService")
public class BannerManageServiceImpl extends EgovAbstractServiceImpl implements BannerManageService {

    @Resource(name = "bannerManageDAO")
    private BannerManageDAO bannerManageDAO;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Resource(name = "fileUtil")
    private FileUtil fileUtil;

    @Override
    public List<BannerItemResponse> selectBannerList(BannerListRequest request) throws Exception {
        request.setDefaultPaging();
        return bannerManageDAO.selectBannerList(request);
    }

    @Override
    public int selectBannerListCount(BannerListRequest request) throws Exception {
        return bannerManageDAO.selectBannerListCount(request);
    }

    @Override
    public BannerDetailDataResponse selectBannerDetail(BannerDetailRequest request) throws Exception {
        return bannerManageDAO.selectBannerDetail(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBanner(BannerSaveRequest request, MultipartFile imageFile) throws Exception {
        request.setBanrCd(bannerManageDAO.createBanrCd());
        setDefaultValues(request);
        saveOrReplaceImage(request, null, imageFile);
        return bannerManageDAO.insertBanner(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBanner(BannerSaveRequest request, MultipartFile imageFile) throws Exception {
        setDefaultValues(request);

        BannerDetailRequest detailRequest = new BannerDetailRequest();
        detailRequest.setBanrCd(request.getBanrCd());
        BannerDetailDataResponse current = bannerManageDAO.selectBannerDetail(detailRequest);

        if (current != null) {
            if (request.getFileCd() == null || request.getFileCd().trim().isEmpty()) {
                request.setFileCd(current.getFileCd() != null ? current.getFileCd() : "");
            }
            if (request.getImgUrl() == null || request.getImgUrl().trim().isEmpty()) {
                request.setImgUrl(current.getImgUrl() != null ? current.getImgUrl() : "");
            }
        }

        boolean removeImage = Boolean.TRUE.equals(request.getRemoveImage());
        if (removeImage && request.getFileCd() != null && !request.getFileCd().trim().isEmpty()) {
            deleteFileGroupQuietly(request.getFileCd());
            request.setFileCd("");
            request.setImgUrl("");
        }

        saveOrReplaceImage(request, current != null ? current.getFileCd() : null, imageFile);
        return bannerManageDAO.updateBanner(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBannerImage(String banrCd, String uniqId) throws Exception {
        BannerDetailRequest detailRequest = new BannerDetailRequest();
        detailRequest.setBanrCd(banrCd);
        BannerDetailDataResponse current = bannerManageDAO.selectBannerDetail(detailRequest);

        if (current != null && current.getFileCd() != null && !current.getFileCd().trim().isEmpty()) {
            deleteFileGroupQuietly(current.getFileCd());
        }

        BannerSaveRequest updateRequest = new BannerSaveRequest();
        updateRequest.setBanrCd(banrCd);
        updateRequest.setUNIQ_ID(uniqId);
        return bannerManageDAO.updateBannerImage(updateRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBanner(String banrCd, String uniqId) throws Exception {
        BannerDetailRequest detailRequest = new BannerDetailRequest();
        detailRequest.setBanrCd(banrCd);
        BannerDetailDataResponse current = bannerManageDAO.selectBannerDetail(detailRequest);

        BannerSaveRequest deleteRequest = new BannerSaveRequest();
        deleteRequest.setBanrCd(banrCd);
        deleteRequest.setUNIQ_ID(uniqId);

        if (current != null && current.getFileCd() != null && !current.getFileCd().trim().isEmpty()) {
            deleteFileGroupQuietly(current.getFileCd());
        }

        return bannerManageDAO.deleteBanner(deleteRequest);
    }

    private void setDefaultValues(BannerSaveRequest request) {
        if (request.getStatCode() == null || request.getStatCode().trim().isEmpty()) {
            request.setStatCode("A");
        }
        if (request.getWidthSize() == null) {
            request.setWidthSize(0);
        }
        if (request.getHeightSize() == null) {
            request.setHeightSize(0);
        }
        if (request.getPosiX() == null) {
            request.setPosiX(0);
        }
        if (request.getPosiY() == null) {
            request.setPosiY(0);
        }
        if (request.getOrderBy() == null) {
            request.setOrderBy(0);
        }
        if (request.getFileCd() == null) {
            request.setFileCd("");
        }
        if (request.getImgUrl() == null) {
            request.setImgUrl("");
        }
    }

    private void saveOrReplaceImage(BannerSaveRequest request, String oldFileCd, MultipartFile imageFile) throws Exception {
        if (imageFile == null || imageFile.getSize() <= 0 || imageFile.getOriginalFilename() == null
                || imageFile.getOriginalFilename().isBlank()) {
            return;
        }

        String targetFileId = request.getFileCd() != null && !request.getFileCd().trim().isEmpty()
                ? request.getFileCd().trim()
                : "";

        if (targetFileId.isEmpty() && oldFileCd != null && !oldFileCd.trim().isEmpty()) {
            targetFileId = oldFileCd.trim();
        }

        if (!targetFileId.isEmpty()) {
            deleteFileGroupQuietly(targetFileId);
        }

        Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
        fileMap.put("bannerImage", imageFile);

        FileDTO fileDTO = new FileDTO();
        List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, targetFileId.isEmpty() ? null : targetFileId, "FILE_", 1, "banr");
        if (fileList.isEmpty()) {
            return;
        }

        String uniqId = request.getUNIQ_ID() != null ? request.getUNIQ_ID() : "";
        for (FileDTO fileInfo : fileList) {
            fileInfo.setUNIQ_ID(uniqId);
            fileManageService.insertFileInfo(fileInfo);
        }

        FileDTO saved = fileList.get(0);
        request.setFileCd(String.valueOf(saved.getFileId()));
        request.setImgUrl(saved.getFilePath() + "/" + saved.getSaveNm());
    }

    private void deleteFileGroupQuietly(String fileCd) {
        try {
            fileManageService.deleteFileGroup(Long.parseLong(fileCd.trim()));
        } catch (Exception ignored) {
            // Ignore: if file does not exist or cannot be parsed, banner row update/delete should still proceed.
        }
    }
}
