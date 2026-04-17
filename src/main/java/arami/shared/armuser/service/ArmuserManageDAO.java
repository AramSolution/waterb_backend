package arami.shared.armuser.service;

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
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ARMUSER(공통 사용자) DAO
 */
@Repository("armuserManageDAO")
public class ArmuserManageDAO extends EgovAbstractMapper {

    public List<ArmuserDTO> selectList(ArmuserListRequest request) {
        return selectList("armuserManageDAO.selectList", request);
    }

    public int selectListCount(ArmuserListRequest request) {
        return selectOne("armuserManageDAO.selectListCount", request);
    }

    /** 사용자웹 학원조회: 정상 학원(ANR, P) 전부 + 과목(콤마/공백) */
    public List<AcademyListForUserItem> selectAcademyListForUserWeb(AcademyListForUserRequest request) {
        return selectList("armuserManageDAO.selectAcademyListForUserWeb", request);
    }

    public int selectAcademyListForUserWebCount(AcademyListForUserRequest request) {
        return selectOne("armuserManageDAO.selectAcademyListForUserWebCount", request);
    }

    public List<ArmuserDTO> selectExcelList(ArmuserListRequest request) {
        return selectList("armuserManageDAO.selectExcelList", request);
    }

    public ArmuserCrtfcDnValueCheckResponse selectCrtfcDnValueCheck(String crtfcDnValue) {
        return selectOne("armuserManageDAO.selectCrtfcDnValueCheck", crtfcDnValue);
    }

    public ArmuserUserIdCheckResponse selectUserIdCheck(String userId) {
        return selectOne("armuserManageDAO.selectUserIdCheck", userId);
    }

    public ArmuserDTO selectDetail(ArmuserDetailRequest request) {
        return selectOne("armuserManageDAO.selectDetail", request);
    }

    public ArmuserDTO selectAcademyMainDetail(ArmuserDetailRequest request) {
        return selectOne("armuserManageDAO.selectAcademyMainDetail", request);
    }

    public int insertArmuser(ArmuserInsertRequest request) {
        return insert("armuserManageDAO.insertArmuser", request);
    }

    public int updateArmuser(ArmuserUpdateRequest request) {
        return update("armuserManageDAO.updateArmuser", request);
    }

    /** 회원 탈퇴(삭제) - MBER_STTUS='D', SECSN_DE 설정 */
    public int deleteArmuser(ArmuserDeleteRequest request) {
        return update("armuserManageDAO.deleteArmuser", request);
    }

    /** USER_PIC(프로필 사진) 비움 - 해당 회원의 USER_PIC 컬럼 초기화 */
    public int clearUserPic(String esntlId) {
        return update("armuserManageDAO.clearUserPic", esntlId);
    }

    /** ATTA_FILE(첨부파일) 비움 - 해당 회원의 ATTA_FILE 컬럼 초기화 */
    public int clearAttaFile(String esntlId) {
        return update("armuserManageDAO.clearAttaFile", esntlId);
    }

    /** BIZNO_FILE(사업자등록증) 비움 - 해당 회원의 BIZNO_FILE 컬럼 초기화 */
    public int clearBiznoFile(String esntlId) {
        return update("armuserManageDAO.clearBiznoFile", esntlId);
    }
}
