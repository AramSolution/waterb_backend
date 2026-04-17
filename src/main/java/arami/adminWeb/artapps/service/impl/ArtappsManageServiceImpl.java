package arami.adminWeb.artapps.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import arami.adminWeb.artapps.service.ArtappsManageDAO;
import arami.adminWeb.artapps.service.ArtappsManageService;
import arami.adminWeb.artapps.service.dto.request.ArtappsApplicationListRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsDeleteByReqIdRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsDetailRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsInsertRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsListRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsUpdateRequest;
import arami.adminWeb.artapps.service.dto.response.ArtappsApplicationListRowDTO;
import arami.adminWeb.artapps.service.dto.response.ArtappsDTO;
import arami.adminWeb.artapps.service.dto.response.ArtappsDetailResponse;
import arami.adminWeb.artapps.service.dto.response.ArtappsResultResponse;
import arami.adminWeb.artprom.service.ArtpromManageService;
import arami.adminWeb.artprom.service.dto.request.ArtpromDetailRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromFileDeleteRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromListRequest;
import arami.adminWeb.artprom.service.dto.response.ArtpromDTO;
import arami.adminWeb.artprom.service.dto.response.ArtpromDetailResponse;
import egovframework.com.cmm.EgovMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공부의명수 관리 서비스.
 * <ul>
 *   <li>사업(ARTPROM) CRUD·파일: {@link arami.adminWeb.artprom.service.ArtpromManageService} / {@code artpromManageDAO}</li>
 *   <li>신청(ARTAPPS) 건수·등록: {@link ArtappsManageDAO} / {@code artappsManageDAO}</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArtappsManageServiceImpl implements ArtappsManageService {

    private static final String ARTAPPS_PRO_GB = "08";

    private final ArtappsManageDAO artappsManageDAO;
    private final ArtpromManageService artpromManageService;
    private final EgovMessageSource egovMessageSource;

    @Override
    public List<ArtappsDTO> selectList(ArtappsListRequest request) throws Exception {
        applyDefaults(request);
        ArtpromListRequest prom = toArtpromListRequest(request);
        List<ArtpromDTO> list = artpromManageService.selectArtpromList(prom);
        return toArtappsDtoList(list);
    }

    @Override
    public int selectListCount(ArtappsListRequest request) throws Exception {
        applyDefaults(request);
        ArtpromListRequest prom = toArtpromListRequest(request);
        return artpromManageService.selectArtpromListCount(prom);
    }

    @Override
    public List<ArtappsDTO> selectExcelList(ArtappsListRequest request) throws Exception {
        applyDefaults(request);
        ArtpromListRequest prom = toArtpromListRequest(request);
        List<ArtpromDTO> list = artpromManageService.selectArtpromExcelList(prom);
        return toArtappsDtoList(list);
    }

    @Override
    public ArtappsDetailResponse selectDetail(String proId) throws Exception {
        ArtpromDetailRequest req = new ArtpromDetailRequest();
        req.setProId(proId);
        ArtpromDetailResponse src = artpromManageService.selectArtpromDetailResponse(req);
        return toArtappsDetailResponse(src);
    }

    @Override
    public ArtappsDetailResponse selectDetailResponse(ArtappsDetailRequest request) throws Exception {
        ArtpromDetailRequest req = new ArtpromDetailRequest();
        BeanUtils.copyProperties(request, req);
        ArtpromDetailResponse src = artpromManageService.selectArtpromDetailResponse(req);
        return toArtappsDetailResponse(src);
    }

    @Override
    public String getNextProId() throws Exception {
        return artpromManageService.getNextProId();
    }

    @Override
    public ArtappsResultResponse insertArtapps(ArtappsInsertRequest request) throws Exception {
        request.setProGb(request.getProGb() != null ? request.getProGb().trim() : ARTAPPS_PRO_GB);
        if (!StringUtils.hasText(request.getUNIQ_ID())) {
            request.setUNIQ_ID("");
        }
        if (!StringUtils.hasText(request.getFileId())) {
            request.setFileId("");
        }
        if (!StringUtils.hasText(request.getReqId())) {
            request.setReqId(artappsManageDAO.getNextReqIdForArtapps());
        }
        if (!StringUtils.hasText(request.getReqAppsId())) {
            request.setReqAppsId(request.getReqId());
        }
        if (!StringUtils.hasText(request.getReqProSeq())) {
            request.setReqProSeq(StringUtils.hasText(request.getProSeq()) ? request.getProSeq().trim() : "0");
        }
        if (!StringUtils.hasText(request.getProSeq())) {
            request.setProSeq("0");
        }
        if (!StringUtils.hasText(request.getSttusCode())) {
            request.setSttusCode("02");
        }
        artappsManageDAO.insertArtapps(request);

        ArtappsResultResponse response = new ArtappsResultResponse();
        response.setResult("00");
        response.setMessage(egovMessageSource.getMessage("success.common.insert"));
        return response;
    }

    @Override
    public ArtappsResultResponse updateArtapps(ArtappsUpdateRequest request) throws Exception {
        request.setProGb(request.getProGb() != null ? request.getProGb().trim() : ARTAPPS_PRO_GB);
        if (!StringUtils.hasText(request.getUNIQ_ID())) {
            request.setUNIQ_ID("");
        }
        if (!StringUtils.hasText(request.getReqId())) {
            throw new IllegalArgumentException("reqId is required");
        }
        artappsManageDAO.updateArtapps(request);

        ArtappsResultResponse response = new ArtappsResultResponse();
        response.setResult("00");
        response.setMessage(egovMessageSource.getMessage("success.common.update"));
        return response;
    }

    @Override
    public ArtappsResultResponse delete(String proId, String uniqId) throws Exception {
        ArtpromDetailRequest req = new ArtpromDetailRequest();
        req.setProId(proId);
        req.setUNIQ_ID(uniqId);
        artpromManageService.deleteArtprom(req);

        ArtappsResultResponse response = new ArtappsResultResponse();
        response.setResult("00");
        response.setMessage(egovMessageSource.getMessage("success.common.delete"));
        return response;
    }

    @Override
    public ArtappsResultResponse deleteProFile(String proId, Long fileId, Integer seq) throws Exception {
        ArtpromFileDeleteRequest req = new ArtpromFileDeleteRequest();
        req.setProId(proId);
        req.setFileId(fileId);
        req.setSeq(seq);
        artpromManageService.deleteProFile(req);

        ArtappsResultResponse response = new ArtappsResultResponse();
        response.setResult("00");
        response.setMessage(egovMessageSource.getMessage("success.common.delete"));
        return response;
    }

    @Override
    public ArtappsResultResponse deleteFile(String proId, Long fileId, Integer seq) throws Exception {
        ArtpromFileDeleteRequest req = new ArtpromFileDeleteRequest();
        req.setProId(proId);
        req.setFileId(fileId);
        req.setSeq(seq);
        artpromManageService.deleteFile(req);

        ArtappsResultResponse response = new ArtappsResultResponse();
        response.setResult("00");
        response.setMessage(egovMessageSource.getMessage("success.common.delete"));
        return response;
    }

    @Override
    public int countArtappsByProIdProSeq(ArtappsInsertRequest request) throws Exception {
        return artappsManageDAO.countArtappsByProIdProSeq(request);
    }

    @Override
    public ArtappsResultResponse deleteApplicationsByReqId(String reqId) throws Exception {
        if (!StringUtils.hasText(reqId)) {
            throw new IllegalArgumentException("reqId is required");
        }
        artappsManageDAO.deleteArtappsApplicationsByReqId(
                ArtappsDeleteByReqIdRequest.builder().reqId(reqId.trim()).build());
        ArtappsResultResponse response = new ArtappsResultResponse();
        response.setResult("00");
        response.setMessage(egovMessageSource.getMessage("success.common.delete"));
        return response;
    }

    @Override
    public void updateArtappsSttusCodeByReqId(String reqId, String sttusCode, String reaDesc) throws Exception {
        if (!StringUtils.hasText(reqId) || !StringUtils.hasText(sttusCode)) {
            return;
        }
        artappsManageDAO.updateArtappsSttusCodeByReqId(reqId.trim(), sttusCode.trim(), reaDesc);
    }

    @Override
    public int countArtappsApplicationList(ArtappsApplicationListRequest request) throws Exception {
        return artappsManageDAO.countArtappsApplicationList(request);
    }

    @Override
    public List<ArtappsApplicationListRowDTO> selectArtappsApplicationList(
            ArtappsApplicationListRequest request) throws Exception {
        request.setDefaultPaging();
        return artappsManageDAO.selectArtappsApplicationList(request);
    }

    private static ArtpromListRequest toArtpromListRequest(ArtappsListRequest request) {
        ArtpromListRequest prom = new ArtpromListRequest();
        BeanUtils.copyProperties(request, prom);
        return prom;
    }

    private static List<ArtappsDTO> toArtappsDtoList(List<ArtpromDTO> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<ArtappsDTO> out = new ArrayList<>(list.size());
        for (ArtpromDTO dto : list) {
            out.add(toArtappsDto(dto));
        }
        return out;
    }

    private static ArtappsDTO toArtappsDto(ArtpromDTO src) {
        if (src == null) {
            return null;
        }
        ArtappsDTO t = new ArtappsDTO();
        BeanUtils.copyProperties(src, t);
        return t;
    }

    private static ArtappsDetailResponse toArtappsDetailResponse(ArtpromDetailResponse src) {
        if (src == null) {
            return null;
        }
        ArtappsDetailResponse t = new ArtappsDetailResponse();
        t.setDetail(toArtappsDto(src.getDetail()));
        t.setProFileList(src.getProFileList());
        t.setFileList(src.getFileList());
        t.setResult(src.getResult());
        return t;
    }

    private void applyDefaults(ArtappsListRequest request) {
        if (request == null) {
            return;
        }
        request.setSearchProGb(request.getSearchProGb() != null ? request.getSearchProGb().trim() : ARTAPPS_PRO_GB);
        request.setDefaultPaging();
    }
}
