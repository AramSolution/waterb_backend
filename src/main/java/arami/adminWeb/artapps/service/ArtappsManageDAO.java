package arami.adminWeb.artapps.service;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.adminWeb.artappm.service.dto.request.ArtappmUpdateRequest;
import java.util.List;

import arami.adminWeb.artapps.service.dto.request.ArtappsApplicationListRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsDeleteByReqIdRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsInsertRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsUpdateRequest;
import arami.adminWeb.artapps.service.dto.response.ArtappsApplicationListRowDTO;

/**
 * 공부의명수(ARTAPPS) MyBatis DAO.
 * 매퍼: {@code classpath:egovframework/mapper/arami/adminWeb/artapps/ArtappsManage_SQL_mysql.xml} (namespace {@code artappsManageDAO}).
 */
@Repository("artappsManageDAO")
public class ArtappsManageDAO extends EgovAbstractMapper {

    /** 지원사업신청ID(REQ_ID) 채번: ARTAPPM + ARTAPPS 전체 MAX 기반 */
    public String getNextReqIdForArtapps() throws Exception {
        return selectOne("artappsManageDAO.getNextReqIdForArtapps");
    }

    /** 해당 사업(PRO_ID)의 ARTAPPS 신청 건수 (STTUS_CODE 03, 04) */
    public int countArtappsByProIdProSeq(ArtappsInsertRequest request) throws Exception {
        Integer val = selectOne("artappsManageDAO.countArtappsByProIdProSeq", request);
        return val != null ? val : 0;
    }

    /** ARTAPPM + ARTAPPS 공부의명수 신청 등록 */
    public int insertArtapps(ArtappsInsertRequest request) throws Exception {
        return insert("artappsManageDAO.insertArtapps", request);
    }

    /** ARTAPPM + ARTAPPS 공부의명수 신청 수정 */
    public int updateArtapps(ArtappsUpdateRequest request) throws Exception {
        return update("artappsManageDAO.updateArtapps", request);
    }

    public int updateArtappsRowByReqId(ArtappmUpdateRequest request) throws Exception {
        return update("artappsManageDAO.updateArtappsRowByReqId", request);
    }

    /** REQ_ID 기준 ARTAPPM + ARTAPPS 상태 코드 동시 변경 */
    public int updateArtappsSttusCodeByReqId(String reqId, String sttusCode, String reaDesc) throws Exception {
        java.util.Map<String, String> param = new java.util.HashMap<>();
        param.put("reqId", reqId);
        param.put("sttusCode", sttusCode);
        param.put("reaDesc", reaDesc != null ? reaDesc : "");
        return update("artappsManageDAO.updateArtappsSttusCodeByReqId", param);
    }

    /** REQ_ID 기준 ARTAPPS·ARTAPPM 신청 1건 삭제 */
    public int deleteArtappsApplicationsByReqId(ArtappsDeleteByReqIdRequest request) throws Exception {
        return update("artappsManageDAO.deleteArtappsApplicationsByReqId", request);
    }

    /** ARTAPPM INNER JOIN ARTAPPS(REQ_ID) 신청목록 건수 */
    public int countArtappsApplicationList(ArtappsApplicationListRequest request) throws Exception {
        Integer val = selectOne("artappsManageDAO.countArtappsApplicationList", request);
        return val != null ? val : 0;
    }

    /** ARTAPPM INNER JOIN ARTAPPS(REQ_ID) 신청목록 */
    public List<ArtappsApplicationListRowDTO> selectArtappsApplicationList(
            ArtappsApplicationListRequest request) throws Exception {
        return selectList("artappsManageDAO.selectArtappsApplicationList", request);
    }
}
