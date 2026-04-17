package arami.userWeb.artprom.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import egovframework.com.cmm.LoginVO;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import arami.userWeb.artprom.service.ArtpromUserDAO;
import arami.userWeb.artprom.service.ArtpromUserService;
import arami.userWeb.artprom.service.dto.request.ArtpromUserDetailRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserFavoriteRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMainCardListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMyAppliedListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMyFavoriteListRequest;
import arami.userWeb.artprom.service.dto.response.ArtpromMentorWorkProjectItem;
import arami.userWeb.artprom.service.dto.response.ArtpromUserDTO;
import arami.userWeb.artprom.service.dto.response.ArtpromUserDetailResponse;
import arami.userWeb.artprom.service.dto.response.ArtpromUserFavoriteResponse;
import arami.userWeb.artprom.service.dto.response.ArtpromUserListDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 지원사업 조회 Service 구현 (사용자웹)
 * userWeb 전용 DAO·XML 사용. 상세 시 파일 목록은 FileManageService로 조회.
 */
@Slf4j
@Service("artpromUserService")
public class ArtpromUserServiceImpl implements ArtpromUserService {

    @Resource(name = "artpromUserDAO")
    private ArtpromUserDAO artpromUserDAO;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Override
    public List<ArtpromUserListDTO> selectArtpromList(ArtpromUserListRequest request) throws Exception {
        applyReqGbPositionFromCurrentUser(request);
        request.setEsntlId(getCurrentUserUniqId());
        sanitizeListProTypes(request);
        request.setDefaultPaging();
        return artpromUserDAO.selectArtpromList(request);
    }

    @Override
    public int selectArtpromListCount(ArtpromUserListRequest request) throws Exception {
        applyReqGbPositionFromCurrentUser(request);
        request.setEsntlId(getCurrentUserUniqId());
        sanitizeListProTypes(request);
        return artpromUserDAO.selectArtpromListCount(request);
    }

    /** listProTypes — 01·02·03만 허용, 그 외 제거. 전부 제거되면 null로 두어 전체 조회 */
    private void sanitizeListProTypes(ArtpromUserListRequest request) {
        List<String> raw = request.getListProTypes();
        if (raw == null || raw.isEmpty()) {
            request.setListProTypes(null);
            return;
        }
        List<String> filtered = raw.stream()
                .map(String::trim)
                .filter(s -> "01".equals(s) || "02".equals(s) || "03".equals(s))
                .distinct()
                .collect(Collectors.toList());
        request.setListProTypes(filtered.isEmpty() ? null : filtered);
    }

    /**
     * REQ_GB 필터 위치 설정: 요청에 이미 있으면 유지, 로그인 사용자는 userSe로 설정, 비로그인은 기본 1(학생).
     * REQ_GB 순서: 1=학생, 2=학부모, 3=학원, 4=멘토, 5=학교.
     */
    private void applyReqGbPositionFromCurrentUser(ArtpromUserListRequest request) {
        if (request.getReqGbPosition() != null) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginVO) {
            String userSe = ((LoginVO) authentication.getPrincipal()).getUserSe();
            if (userSe != null && !userSe.isBlank()) {
                Integer position = reqGbPositionByUserSe(userSe.trim());
                if (position != null) {
                    request.setReqGbPosition(position);
                    return;
                }
            }
        }
        request.setReqGbPosition(1);
    }

    /** userSe → REQ_GB 위치(1=학생, 2=학부모, 3=학원, 4=멘토, 5=학교). 매핑 확장 시 여기만 수정. */
    private Integer reqGbPositionByUserSe(String userSe) {
        return switch (userSe) {
            case "SNR" -> 1;  // 학생
            case "PNR" -> 2;  // 학부모
            case "ANT" -> 3;  // 학원
            case "MNR" -> 4;  // 멘토
            case "SCH" -> 5;  // 학교
            default -> null;
        };
    }

    @Override
    public List<ArtpromUserListDTO> selectMyAppliedArtpromList(ArtpromUserMyAppliedListRequest request) throws Exception {
        String reqEsntlId = getCurrentUserUniqId();
        if (reqEsntlId == null || reqEsntlId.isBlank()) {
            return new ArrayList<>();
        }
        String userSe = getCurrentUserSe();
        if (userSe == null || userSe.isBlank()) {
            return new ArrayList<>();
        }
        request.setReqEsntlId(reqEsntlId);
        request.setUserSe(userSe.trim());
        return artpromUserDAO.selectMyAppliedArtpromList(request);
    }

    @Override
    public int selectMyAppliedArtpromListCount(ArtpromUserMyAppliedListRequest request) throws Exception {
        String reqEsntlId = getCurrentUserUniqId();
        if (reqEsntlId == null || reqEsntlId.isBlank()) {
            return 0;
        }
        String userSe = getCurrentUserSe();
        if (userSe == null || userSe.isBlank()) {
            return 0;
        }
        request.setReqEsntlId(reqEsntlId);
        request.setUserSe(userSe.trim());
        return artpromUserDAO.selectMyAppliedArtpromListCount(request);
    }

    @Override
    public List<ArtpromUserListDTO> selectMyAppliedArtpromListMentor(ArtpromUserMyAppliedListRequest request) throws Exception {
        String reqEsntlId = getCurrentUserUniqId();
        if (reqEsntlId == null || reqEsntlId.isBlank()) {
            return new ArrayList<>();
        }
        String userSe = getCurrentUserSe();
        if (!"MNR".equals(userSe != null ? userSe.trim() : "")) {
            return new ArrayList<>();
        }
        request.setReqEsntlId(reqEsntlId);
        request.setUserSe("MNR");
        return artpromUserDAO.selectMyAppliedArtpromListMentor(request);
    }

    @Override
    public int selectMyAppliedArtpromListMentorCount(ArtpromUserMyAppliedListRequest request) throws Exception {
        String reqEsntlId = getCurrentUserUniqId();
        if (reqEsntlId == null || reqEsntlId.isBlank()) {
            return 0;
        }
        String userSe = getCurrentUserSe();
        if (!"MNR".equals(userSe != null ? userSe.trim() : "")) {
            return 0;
        }
        request.setReqEsntlId(reqEsntlId);
        request.setUserSe("MNR");
        return artpromUserDAO.selectMyAppliedArtpromListMentorCount(request);
    }

    @Override
    public List<ArtpromUserListDTO> selectMyFavoriteArtpromList(ArtpromUserMyFavoriteListRequest request) throws Exception {
        String reqEsntlId = getCurrentUserUniqId();
        if (reqEsntlId == null || reqEsntlId.isBlank()) {
            return new ArrayList<>();
        }
        request.setReqEsntlId(reqEsntlId.trim());
        request.setDefaultPaging();
        return artpromUserDAO.selectMyFavoriteArtpromList(request);
    }

    @Override
    public int selectMyFavoriteArtpromListCount(ArtpromUserMyFavoriteListRequest request) throws Exception {
        String reqEsntlId = getCurrentUserUniqId();
        if (reqEsntlId == null || reqEsntlId.isBlank()) {
            return 0;
        }
        request.setReqEsntlId(reqEsntlId.trim());
        return artpromUserDAO.selectMyFavoriteArtpromListCount(request);
    }

    @Override
    public List<ArtpromUserListDTO> selectArtpromMainCardList(ArtpromUserMainCardListRequest request) throws Exception {
        if (request == null) {
            request = new ArtpromUserMainCardListRequest();
        }
        if (request.getIncludePromo() == null) {
            request.setIncludePromo(Boolean.FALSE);
        }
        return artpromUserDAO.selectArtpromMainCardList(request);
    }

    /** 로그인 사용자 고유ID. 비로그인 시 빈 문자열. */
    private String getCurrentUserUniqId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginVO) {
            String uniqId = ((LoginVO) authentication.getPrincipal()).getUniqId();
            return uniqId != null ? uniqId : "";
        }
        return "";
    }

    /** 로그인 사용자 구분(SNR/PNR/ANT/MNR). 비로그인 시 빈 문자열. */
    private String getCurrentUserSe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginVO) {
            String userSe = ((LoginVO) authentication.getPrincipal()).getUserSe();
            return userSe != null ? userSe : "";
        }
        return "";
    }

    @Override
    public ArtpromUserDetailResponse selectArtpromDetailResponse(ArtpromUserDetailRequest request) throws Exception {
        ArtpromUserDTO detail = artpromUserDAO.selectArtpromDetail(request);
        ArtpromUserDetailResponse response = new ArtpromUserDetailResponse();
        response.setDetail(detail);

        List<Map<String, Object>> proFileList = new ArrayList<>();
        List<Map<String, Object>> fileList = new ArrayList<>();
        if (detail != null) {
            String proFileIdStr = detail.getProFileId();
            if (proFileIdStr != null && !proFileIdStr.trim().isEmpty()) {
                try {
                    Long proFileId = Long.parseLong(proFileIdStr.trim());
                    proFileList.addAll(toFileMapList(fileManageService.selectFileListByFileId(proFileId)));
                } catch (NumberFormatException e) {
                    log.warn("selectArtpromDetailResponse: invalid proFileId, skip proFileList. proFileId={}", proFileIdStr);
                }
            }
            String fileIdStr = detail.getFileId();
            if (fileIdStr != null && !fileIdStr.trim().isEmpty()) {
                try {
                    Long fileId = Long.parseLong(fileIdStr.trim());
                    fileList.addAll(toFileMapList(fileManageService.selectFileListByFileId(fileId)));
                } catch (NumberFormatException e) {
                    log.warn("selectArtpromDetailResponse: invalid fileId, skip fileList. fileId={}", fileIdStr);
                }
            }
        }
        response.setProFileList(proFileList);
        response.setFileList(fileList);
        return response;
    }

    private List<Map<String, Object>> toFileMapList(List<FileDTO> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (FileDTO f : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("fileId", String.valueOf(f.getFileId()));
            m.put("seq", String.valueOf(f.getSeq()));
            m.put("orgfNm", f.getOrgfNm());
            m.put("saveNm", f.getSaveNm());
            m.put("filePath", f.getFilePath());
            m.put("fileExt", f.getFileExt());
            m.put("fileSize", Float.valueOf(f.getFileSize()));
            m.put("fileType", f.getFileType());
            m.put("sttusCode", f.getSttusCode());
            result.add(m);
        }
        return result;
    }

    @Override
    public List<ArtpromMentorWorkProjectItem> getMentorWorkProjectList() throws Exception {
        return artpromUserDAO.selectMentorWorkProjectList();
    }

    @Override
    @Transactional
    public ArtpromUserFavoriteResponse saveFavoriteArtprom(ArtpromUserFavoriteRequest request) throws Exception {
        ArtpromUserFavoriteResponse response = new ArtpromUserFavoriteResponse();

        String proId = request != null && request.getProId() != null ? request.getProId().trim() : "";
        String esntlId = getCurrentUserUniqId();

        if (proId.isEmpty()) {
            response.setResult("40");
            response.setMessage("지원사업 코드가 필요합니다.");
            return response;
        }

        if (esntlId == null || esntlId.isBlank()) {
            response.setResult("41");
            response.setMessage("로그인이 필요합니다.");
            return response;
        }

        ArtpromUserFavoriteRequest favoriteRequest = new ArtpromUserFavoriteRequest();
        favoriteRequest.setProId(proId);
        favoriteRequest.setEsntlId(esntlId.trim());

        artpromUserDAO.deleteArtmark(favoriteRequest);
        artpromUserDAO.insertArtmark(favoriteRequest);

        response.setResult("00");
        response.setMessage("정상적으로 저장되었습니다.");
        return response;
    }

    @Override
    @Transactional
    public ArtpromUserFavoriteResponse deleteFavoriteArtprom(String proId) throws Exception {
        ArtpromUserFavoriteResponse response = new ArtpromUserFavoriteResponse();

        String normalizedProId = proId != null ? proId.trim() : "";
        String esntlId = getCurrentUserUniqId();

        if (normalizedProId.isEmpty()) {
            response.setResult("40");
            response.setMessage("지원사업 코드가 필요합니다.");
            return response;
        }

        if (esntlId == null || esntlId.isBlank()) {
            response.setResult("41");
            response.setMessage("로그인이 필요합니다.");
            return response;
        }

        ArtpromUserFavoriteRequest favoriteRequest = new ArtpromUserFavoriteRequest();
        favoriteRequest.setProId(normalizedProId);
        favoriteRequest.setEsntlId(esntlId.trim());

        artpromUserDAO.deleteArtmark(favoriteRequest);

        response.setResult("00");
        response.setMessage("정상적으로 삭제되었습니다.");
        return response;
    }
}
