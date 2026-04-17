package arami.shared.armuser.service.impl;

import arami.common.userWeb.member.service.MemberUsrService;
import arami.shared.armuser.dto.request.AcademyListForUserRequest;
import arami.shared.armuser.dto.request.ArmuserDetailRequest;
import arami.shared.armuser.dto.request.ArmuserDeleteRequest;
import arami.shared.armuser.dto.request.ArmuserInsertRequest;
import arami.shared.armuser.dto.request.ArmuserListRequest;
import arami.shared.armuser.dto.request.ArmuserUpdateRequest;
import arami.shared.armuser.dto.response.AcademyListForUserItem;
import arami.shared.armuser.dto.response.ArmuserDTO;
import arami.shared.armuser.dto.response.ArmuserCrtfcDnValueCheckResponse;
import arami.shared.armuser.dto.response.ArmuserUserIdCheckResponse;
import arami.shared.armuser.dto.response.ArmuserResultResponse;
import arami.shared.armuser.service.ArmuserManageDAO;
import arami.shared.armuser.service.ArmuserService;
import arami.common.error.BusinessException;
import arami.common.error.ErrorCode;
import arami.common.files.service.FileManageService;
import arami.common.files.service.FileDTO;
import egovframework.let.utl.sim.service.EgovFileScrty;
import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ARMUSER(공통 사용자) Service 구현
 */
@Service("armuserService")
public class ArmuserServiceImpl extends EgovAbstractServiceImpl implements ArmuserService {

    @Resource(name = "armuserManageDAO")
    private ArmuserManageDAO armuserManageDAO;

    @Resource(name = "memberUsrService")
    private MemberUsrService memberUsrService;

    @Resource(name = "egovUsrCnfrmIdGnrService")
    private EgovIdGnrService idgenService;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Override
    public List<ArmuserDTO> selectList(ArmuserListRequest request) {
        request.setDefaultPaging();
        return armuserManageDAO.selectList(request);
    }

    @Override
    public int selectListCount(ArmuserListRequest request) {
        request.setDefaultPaging();
        return armuserManageDAO.selectListCount(request);
    }

    @Override
    public List<AcademyListForUserItem> selectAcademyListForUserWeb(AcademyListForUserRequest request) {
        request.setDefaultPaging();
        return armuserManageDAO.selectAcademyListForUserWeb(request);
    }

    @Override
    public int selectAcademyListForUserWebCount(AcademyListForUserRequest request) {
        request.setDefaultPaging();
        return armuserManageDAO.selectAcademyListForUserWebCount(request);
    }

    @Override
    public List<ArmuserDTO> selectExcelList(ArmuserListRequest request) {
        request.setDefaultPaging();
        return armuserManageDAO.selectExcelList(request);
    }

    @Override
    public ArmuserCrtfcDnValueCheckResponse selectCrtfcDnValueCheck(String crtfcDnValue) {
        ArmuserCrtfcDnValueCheckResponse response = new ArmuserCrtfcDnValueCheckResponse();
        
        // trim 처리로 앞뒤 공백/줄바꿈 제거
        String trimmedValue = crtfcDnValue != null ? crtfcDnValue.trim() : null;
        
        if (!StringUtils.hasText(trimmedValue)) {
            response.setExist(0);
            response.setResult("01");
            response.setMessage("개인식별코드가 없습니다.");
            return response;
        }

        try {
            response = armuserManageDAO.selectCrtfcDnValueCheck(trimmedValue);
            if (response == null) {
                response = new ArmuserCrtfcDnValueCheckResponse();
                response.setExist(0);
                response.setResult("00");
                response.setMessage("개인식별코드가 존재하지 않습니다.");
                return response;
            }
            response.setExist(1);
            response.setResult("00");
            response.setMessage("개인식별코드가 존재합니다.");
        } catch (Exception e) {
            response.setExist(0);
            response.setResult("01");
            response.setMessage(e.getMessage() != null ? e.getMessage() : "조회에 실패했습니다.");
            return response;
        }
        
        return response;
    }         

    @Override
    public ArmuserUserIdCheckResponse selectUserIdCheck(String userId) {
        ArmuserUserIdCheckResponse response = new ArmuserUserIdCheckResponse();

        // trim 처리로 앞뒤 공백/줄바꿈 제거
        String trimmedValue = userId != null ? userId.trim() : null;
        
        if (!StringUtils.hasText(trimmedValue)) {
            response.setExist(0);
            response.setResult("01");
            response.setMessage("회원ID가 없습니다.");
            return response;    
        }

        try {
            response = armuserManageDAO.selectUserIdCheck(trimmedValue);
            if (response == null) {
                response = new ArmuserUserIdCheckResponse();
                response.setExist(0);
                response.setResult("00");
                response.setMessage("회원ID가 존재하지 않습니다.");
                return response;
            }
            response.setResult("00");
            response.setMessage("회원ID가 존재합니다.");
        } catch (Exception e) {
            response.setExist(0);
            response.setResult("01");
            response.setMessage(e.getMessage() != null ? e.getMessage() : "조회에 실패했습니다.");
            return response;
        }

        return response;
    }

    @Override
    public ArmuserDTO selectDetail(ArmuserDetailRequest request) {
        return armuserManageDAO.selectDetail(request);
    }

    @Override
    public ArmuserDTO selectAcademyMainDetail(ArmuserDetailRequest request) {
        return armuserManageDAO.selectAcademyMainDetail(request);
    }

    @Override
    public ArmuserResultResponse insertArmuser(ArmuserInsertRequest request) {
        if (request == null) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage("요청 정보가 없습니다.");
            return r;
        }
        if (!StringUtils.hasText(request.getUserId())) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage("회원ID가 없습니다.");
            return r;
        }
        // [1] 아이디 중복 체크 (ARMUSER.USER_ID)
        try {
            Map<String, String> checkModel = new HashMap<>();
            checkModel.put("userId", request.getUserId());
            String checkYn = memberUsrService.checkMemberId(checkModel);
            if ("N".equals(checkYn)) {
                ArmuserResultResponse r = new ArmuserResultResponse();
                r.setResult("50");
                r.setMessage("중복되는 아이디가 있습니다. 다른 아이디를 사용하여 주십시요.");
                return r;
            }
        } catch (Exception e) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage(e.getMessage() != null ? e.getMessage() : "아이디 중복 확인에 실패했습니다.");
            return r;
        }
        // [2] esntlId 채번 (등록 시 항상 idgenService로 생성, 화면에서 받지 않음)
        try {
            request.setEsntlId(idgenService.getNextStringId());
        } catch (Exception e) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage(e.getMessage() != null ? e.getMessage() : "고유ID 생성에 실패했습니다.");
            return r;
        }
        // [3] 비밀번호 암호화 (EgovFileScrty)
        if (StringUtils.hasText(request.getPassword()) && StringUtils.hasText(request.getUserId())) {
            try {
                request.setPassword(EgovFileScrty.encryptPassword(request.getPassword(), request.getUserId()));
            } catch (Exception e) {
                ArmuserResultResponse r = new ArmuserResultResponse();
                r.setResult("01");
                r.setMessage(e.getMessage() != null ? e.getMessage() : "비밀번호 암호화에 실패했습니다.");
                return r;
            }
        }
        if (!StringUtils.hasText(request.getMberSttus())) {
            request.setMberSttus("P");
        }
        if (!StringUtils.hasText(request.getGroupId())) {
            request.setGroupId("GROUP_00000000000000");
        }
        try {
            armuserManageDAO.insertArmuser(request);
            return null;
        } catch (Exception e) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage(e.getMessage() != null ? e.getMessage() : "등록에 실패했습니다.");
            return r;
        }
    }

    @Override
    public ArmuserResultResponse updateArmuser(ArmuserUpdateRequest request) {
        if (request == null || !StringUtils.hasText(request.getEsntlId())) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage("고유ID가 없습니다.");
            return r;
        }
        // 비밀번호 변경 시 암호화 (EgovFileScrty)
        if (StringUtils.hasText(request.getPassword()) && StringUtils.hasText(request.getUserId())) {
            try {
                request.setPassword(EgovFileScrty.encryptPassword(request.getPassword(), request.getUserId()));
            } catch (Exception e) {
                ArmuserResultResponse r = new ArmuserResultResponse();
                r.setResult("01");
                r.setMessage(e.getMessage() != null ? e.getMessage() : "비밀번호 암호화에 실패했습니다.");
                return r;
            }
        }
        try {
            armuserManageDAO.updateArmuser(request);
            return null;
        } catch (Exception e) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage(e.getMessage() != null ? e.getMessage() : "수정에 실패했습니다.");
            return r;
        }
    }

    @Override
    public ArmuserResultResponse deleteArmuser(ArmuserDeleteRequest request) {
        if (request == null || !StringUtils.hasText(request.getEsntlId())) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage("고유ID가 없습니다.");
            return r;
        }
        try {
            int updated = armuserManageDAO.deleteArmuser(request);
            if (updated == 0) {
                ArmuserResultResponse r = new ArmuserResultResponse();
                r.setResult("01");
                r.setMessage("대상 회원이 없거나 이미 탈퇴 처리되었습니다.");
                return r;
            }
            return null;
        } catch (Exception e) {
            ArmuserResultResponse r = new ArmuserResultResponse();
            r.setResult("01");
            r.setMessage(e.getMessage() != null ? e.getMessage() : "탈퇴 처리에 실패했습니다.");
            return r;
        }
    }

    @Override
    public void deleteUserPic(String esntlId, Long fileId, Integer seq) throws Exception {
        try {
            fileManageService.deleteFile(fileId, seq);
        } catch (BusinessException e) {
            if (e.getErrorCode() != ErrorCode.FILE_NOT_FOUND) {
                throw e;
            }
            // 파일 레코드가 이미 없어도 USER_PIC 참조는 비움(정합성 유지)
        }
        List<FileDTO> remaining = fileManageService.selectFileListByFileId(fileId);
        if (remaining.isEmpty() && esntlId != null && !esntlId.trim().isEmpty()) {
            armuserManageDAO.clearUserPic(esntlId.trim());
        }
    }

    @Override
    public void deleteAttaFile(String esntlId, Long fileId, Integer seq) throws Exception {
        try {
            fileManageService.deleteFile(fileId, seq);
        } catch (BusinessException e) {
            if (e.getErrorCode() != ErrorCode.FILE_NOT_FOUND) {
                throw e;
            }
        }
        List<FileDTO> remaining = fileManageService.selectFileListByFileId(fileId);
        if (remaining.isEmpty() && esntlId != null && !esntlId.trim().isEmpty()) {
            armuserManageDAO.clearAttaFile(esntlId.trim());
        }
    }

    @Override
    public void deleteBiznoFile(String esntlId, Long fileId, Integer seq) throws Exception {
        try {
            fileManageService.deleteFile(fileId, seq);
        } catch (BusinessException e) {
            if (e.getErrorCode() != ErrorCode.FILE_NOT_FOUND) {
                throw e;
            }
        }
        if (esntlId != null && !esntlId.trim().isEmpty()) {
            armuserManageDAO.clearBiznoFile(esntlId.trim());
        }
    }
}
