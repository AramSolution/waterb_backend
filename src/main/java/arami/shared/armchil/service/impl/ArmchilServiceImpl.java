package arami.shared.armchil.service.impl;

import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import arami.shared.armchil.dto.request.ArmchilLinkRequest;
import arami.shared.armchil.dto.response.ArmchilChildDTO;
import arami.shared.armchil.service.ArmchilManageDAO;
import arami.shared.armchil.service.ArmchilService;
import arami.shared.armuser.dto.response.ArmuserResultResponse;
import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ARMCHIL(자녀관리) Service 구현
 */
@Service("armchilService")
public class ArmchilServiceImpl extends EgovAbstractServiceImpl implements ArmchilService {

    @Resource(name = "armchilManageDAO")
    private ArmchilManageDAO armchilManageDAO;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Override
    public List<ArmchilChildDTO> getChildrenByParent(String pEsntlId) {
        if (pEsntlId == null || pEsntlId.isBlank()) {
            return List.of();
        }
        List<ArmchilChildDTO> list = armchilManageDAO.selectChildrenByParent(pEsntlId);
        for (ArmchilChildDTO child : list) {
            if (StringUtils.hasText(child.getUserPic())) {
                try {
                    long fileId = Long.parseLong(child.getUserPic().trim());
                    List<FileDTO> fileList = fileManageService.selectFileListByFileId(fileId);
                    child.setUserPicFiles(fileList != null ? fileList : new ArrayList<>());
                } catch (NumberFormatException e) {
                    child.setUserPicFiles(new ArrayList<>());
                } catch (Exception e) {
                    child.setUserPicFiles(new ArrayList<>());
                }
            } else {
                child.setUserPicFiles(new ArrayList<>());
            }
        }
        return list;
    }

    @Override
    public List<ArmchilChildDTO> getParentsByChild(String cEsntlId) {
        if (cEsntlId == null || cEsntlId.isBlank()) {
            return List.of();
        }
        List<ArmchilChildDTO> list = armchilManageDAO.selectParentsByChild(cEsntlId);
        for (ArmchilChildDTO child : list) {
            if (StringUtils.hasText(child.getUserPic())) {
                try {
                    long fileId = Long.parseLong(child.getUserPic().trim());
                    List<FileDTO> fileList = fileManageService.selectFileListByFileId(fileId);
                    child.setUserPicFiles(fileList != null ? fileList : new ArrayList<>());
                } catch (NumberFormatException e) {
                    child.setUserPicFiles(new ArrayList<>());
                } catch (Exception e) {
                    child.setUserPicFiles(new ArrayList<>());
                }
            } else {
                child.setUserPicFiles(new ArrayList<>());
            }
        }
        return list;
    }

    @Override
    public List<ArmchilChildDTO> getChildrenByParentExcel(String pEsntlId) {
        if (pEsntlId == null || pEsntlId.isBlank()) {
            return List.of();
        }
        return armchilManageDAO.selectChildrenByParentExcel(pEsntlId);
    }

    @Override
    @Transactional
    public ArmuserResultResponse linkChild(String pEsntlId, ArmchilLinkRequest request) {
        ArmuserResultResponse response = new ArmuserResultResponse();
        if (request == null || !StringUtils.hasText(pEsntlId)) {
            response.setResult("01");
            response.setMessage("부모 정보가 없습니다.");
            return response;
        }
        String cEsntlId = armchilManageDAO.selectChildEsntlIdByMatch(request);
        if (!StringUtils.hasText(cEsntlId)) {
            response.setResult("01");
            response.setMessage("해당 자녀가 존재하지 않습니다.");
            return response;
        }
        String linkedParentEsntlId = armchilManageDAO.selectLinkedParentEsntlIdByChild(cEsntlId);
        if (StringUtils.hasText(linkedParentEsntlId)) {
            if (linkedParentEsntlId.equals(pEsntlId)) {
                response.setResult("50");
                response.setMessage("이미 연동된 자녀입니다.");
            } else {
                response.setResult("50");
                response.setMessage("이미 다른 보호자에게 연동된 자녀입니다.");
            }
            return response;
        }
        if (armchilManageDAO.selectExistsArmchil(pEsntlId, cEsntlId) > 0) {
            response.setResult("50");
            response.setMessage("이미 연동된 자녀입니다.");
            return response;
        }
        armchilManageDAO.insertArmchil(pEsntlId, cEsntlId);
        response.setResult("00");
        response.setMessage("연동되었습니다.");
        return response;
    }

    @Override
    @Transactional
    public void deleteChildLink(String pEsntlId, String cEsntlId) {
        if (StringUtils.hasText(pEsntlId) && StringUtils.hasText(cEsntlId)) {
            armchilManageDAO.deleteArmchil(pEsntlId, cEsntlId);
        }
    }
}
