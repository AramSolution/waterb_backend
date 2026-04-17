package arami.shared.armchil.service;

import arami.shared.armchil.dto.request.ArmchilLinkRequest;
import arami.shared.armchil.dto.response.ArmchilChildDTO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ARMCHIL(자녀관리) DAO
 */
@Repository("armchilManageDAO")
public class ArmchilManageDAO extends EgovAbstractMapper {

    /**
     * 학부모(P_ESNTL_ID)의 자녀 목록 조회
     */
    public List<ArmchilChildDTO> selectChildrenByParent(String pEsntlId) {
        return selectList("armchilManageDAO.selectChildrenByParent", pEsntlId);
    }

    /**
     * 자녀(C_ESNTL_ID)의 학부모(보호자) 목록 조회
     */
    public List<ArmchilChildDTO> selectParentsByChild(String cEsntlId) {
        return selectList("armchilManageDAO.selectParentsByChild", cEsntlId);
    }

    /**
     * 학부모(P_ESNTL_ID)의 자녀 목록 조회 - 엑셀 (페이징 없음)
     */
    public List<ArmchilChildDTO> selectChildrenByParentExcel(String pEsntlId) {
        return selectList("armchilManageDAO.selectChildrenByParentExcel", pEsntlId);
    }

    /**
     * 학생명·성별·연락처·주민번호에 일치하는 ARMUSER 1건의 ESNTL_ID 조회 (없으면 null)
     */
    public String selectChildEsntlIdByMatch(ArmchilLinkRequest request) {
        return selectOne("armchilManageDAO.selectChildEsntlIdByMatch", request);
    }

    /**
     * (P_ESNTL_ID, C_ESNTL_ID) 존재 여부
     */
    public int selectExistsArmchil(String pEsntlId, String cEsntlId) {
        Map<String, String> param = new HashMap<>();
        param.put("pEsntlId", pEsntlId);
        param.put("cEsntlId", cEsntlId);
        Integer one = selectOne("armchilManageDAO.selectExistsArmchil", param);
        return one != null ? one : 0;
    }

    /**
     * 자녀(C_ESNTL_ID)가 이미 연동된 부모(P_ESNTL_ID) 1건 조회 (없으면 null)
     */
    public String selectLinkedParentEsntlIdByChild(String cEsntlId) {
        Map<String, String> param = new HashMap<>();
        param.put("cEsntlId", cEsntlId);
        return selectOne("armchilManageDAO.selectLinkedParentEsntlIdByChild", param);
    }

    /**
     * ARMCHIL 1건 INSERT
     */
    public int insertArmchil(String pEsntlId, String cEsntlId) {
        Map<String, String> param = new HashMap<>();
        param.put("pEsntlId", pEsntlId);
        param.put("cEsntlId", cEsntlId);
        return insert("armchilManageDAO.insertArmchil", param);
    }

    /**
     * ARMCHIL 1건 DELETE (P_ESNTL_ID, C_ESNTL_ID)
     */
    public int deleteArmchil(String pEsntlId, String cEsntlId) {
        Map<String, String> param = new HashMap<>();
        param.put("pEsntlId", pEsntlId);
        param.put("cEsntlId", cEsntlId);
        return delete("armchilManageDAO.deleteArmchil", param);
    }
}
