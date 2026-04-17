package arami.adminWeb.artappm.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Repository;
import arami.adminWeb.artappm.service.dto.request.ChangeListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmByStudentRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmClearFileIdParam;
import arami.adminWeb.artappm.service.dto.request.ArtappmClearStudyCertParam;
import arami.adminWeb.artappm.service.dto.request.ArtappmDeleteRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmDetailRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmInsertRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmResultGbUpdateParam;
import arami.adminWeb.artappm.service.dto.request.ArtapmmApplicationByReqIdOnlyParam;
import arami.adminWeb.artappm.service.dto.request.ArtapmmMentorDuplicateParam;
import arami.adminWeb.artappm.service.dto.request.ArtapmmMentorSaveParam;
import arami.adminWeb.artappm.service.dto.request.ArtappmSelectionListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmStudyCertUpdateParam;
import arami.adminWeb.artappm.service.dto.request.ArtappmUpdateRequest;
import arami.adminWeb.artappm.service.dto.response.ArtapmmApplicationListItemResponse;
import arami.adminWeb.artappm.service.dto.response.ArtappmDTO;
import arami.adminWeb.artappm.service.dto.request.StudyCertDetailRequest;
import arami.adminWeb.artappm.service.dto.response.ChangeListItemResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertDetailResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertListItemResponse;

/**
 * 지원사업 신청 관리 DAO
 */
@Repository("artappmManageDAO")
public class ArtappmManageDAO extends EgovAbstractMapper {

    public List<ArtappmDTO> selectArtappmList(ArtappmListRequest request) throws Exception {
        return selectList("artappmManageDAO.selectArtappmList", request);
    }

    public int selectArtappmListCount(ArtappmListRequest request) throws Exception {
        return selectOne("artappmManageDAO.selectArtappmListCount", request);
    }

    public List<ArtappmDTO> selectArtappmExcelList(ArtappmListRequest request) throws Exception {
        return selectList("artappmManageDAO.selectArtappmExcelList", request);
    }

    /** 선정관리용 지원사업 신청 목록 (PK 조건만, 페이징 없음) */
    public List<ArtappmDTO> selectArtappmSelectionList(ArtappmSelectionListRequest request) throws Exception {
        return selectList("artappmManageDAO.selectArtappmSelectionList", request);
    }

    public ArtappmDTO selectArtappmDetail(ArtappmDetailRequest request) throws Exception {
        return selectOne("artappmManageDAO.selectArtappmDetail", request);
    }

    /** 학생·사업별 최신 지원사업 신청 조회 (userWeb bizInput) */
    public ArtappmDTO selectArtappmByStudentAndPro(ArtappmByStudentRequest request) throws Exception {
        return selectOne("artappmManageDAO.selectArtappmByStudentAndPro", request);
    }

    /** PK(PRO_ID, PRO_SEQ, REQ_ESNTL_ID) 기준 존재 건수 (중복 체크용) */
    public int countArtappmByPk(ArtappmInsertRequest request) throws Exception {
        return selectOne("artappmManageDAO.countArtappmByPk", request);
    }

    /** PK(PRO_ID, PRO_SEQ, WORK_DT, REQ_ESNTL_ID) 기준 존재 건수 (03 공공형 슬롯 중복 체크용) */
    public int countArtappmByPkWithWorkDt(ArtappmInsertRequest request) throws Exception {
        return selectOne("artappmManageDAO.countArtappmByPkWithWorkDt", request);
    }

    /** PK(PRO_ID, REQ_ESNTL_ID) 기준 존재 건수 (1회 신청 사업 중복 체크용) */
    public int countArtappmByProIdReqEsntlId(ArtappmInsertRequest request) throws Exception {
        return selectOne("artappmManageDAO.countArtappmByProIdReqEsntlId", request);
    }

    /** 지원사업신청ID(REQ_ID) 채번: ARTAPPM MAX(REQ_ID)+1, PRO_ID와 동일 방식 */
    public String getNextReqId() throws Exception {
        return selectOne("artappmManageDAO.getNextReqId", new EgovMap());
    }

    /** PRO_GB 05/07 인원 마감 체크: ARTPROD 행 FOR UPDATE 후 REC_CNT 반환 (없으면 null) */
    public Integer selectArtprodRecCntForUpdate(ArtappmInsertRequest request) throws Exception {
        return selectOne("artappmManageDAO.selectArtprodRecCntForUpdate", request);
    }

    /** PRO_GB 05/07 인원 마감 체크: 해당 PRO_ID, PRO_SEQ의 신청 건수 (STTUS_CODE 03,04) */
    public int countArtappmByProIdProSeq(ArtappmInsertRequest request) throws Exception {
        Integer val = selectOne("artappmManageDAO.countArtappmByProIdProSeq", request);
        return val != null ? val : 0;
    }

    public int insertArtappm(ArtappmInsertRequest request) throws Exception {
        return insert("artappmManageDAO.insertArtappm", request);
    }

    public int updateArtappm(ArtappmUpdateRequest request) throws Exception {
        return update("artappmManageDAO.updateArtappm", request);
    }

    public int deleteArtappm(ArtappmDeleteRequest request) throws Exception {
        return delete("artappmManageDAO.deleteArtappm", request);
    }

    /** 첨부파일 그룹 비움: 해당 fileId에 파일이 없을 때 ARTAPPM.FILE_ID 초기화 */
    public int clearFileId(ArtappmClearFileIdParam param) throws Exception {
        return update("artappmManageDAO.clearFileId", param);
    }

    /** 수강확인증 파일 ID만 UPDATE (ARTAPPM.STUDY_CERT) */
    public int updateArtappmStudyCert(ArtappmStudyCertUpdateParam param) throws Exception {
        return update("artappmManageDAO.updateArtappmStudyCert", param);
    }

    /** 선정관리: 선정여부(RESULT_GB)만 UPDATE */
    public int updateArtappmResultGb(ArtappmResultGbUpdateParam param) throws Exception {
        return update("artappmManageDAO.updateArtappmResultGb", param);
    }


    /** ARTAPPM 상태만 변경 (REQ_ID 기준). 02=REQ_DT, 03=APRR_DT, 04=완료, 11=반려(reaDesc 사용), 12=STOP_DT. reaDesc는 sttusCode=11일 때만 반영 */
    public int updateArtappmSttusCodeByReqId(String reqId, String sttusCode, String reaDesc) throws Exception {
        java.util.Map<String, String> param = new java.util.HashMap<>();
        param.put("reqId", reqId);
        param.put("sttusCode", sttusCode);
        param.put("reaDesc", reaDesc != null ? reaDesc : "");
        return update("artappmManageDAO.updateArtappmSttusCodeByReqId", param);
    }

    /** 수강확인증 목록: ARTAPPM.STUDY_CERT = ARTFILE.FILE_ID 조인, 한 행 = 수강확인증 1건(파일) */
    public List<StudyCertListItemResponse> selectStudyCertList(ArtappmListRequest request) throws Exception {
        return selectList("artappmManageDAO.selectStudyCertList", request);
    }

    /** 수강확인증 전체 목록(사용자페이지용): 동일 조회, 페이징 없음 */
    public List<StudyCertListItemResponse> selectStudyCertListAll(ArtappmListRequest request) throws Exception {
        return selectList("artappmManageDAO.selectStudyCertListAll", request);
    }

    public int selectStudyCertListCount(ArtappmListRequest request) throws Exception {
        return selectOne("artappmManageDAO.selectStudyCertListCount", request);
    }

    /** 수강확인증 엑셀 목록: 동일 조회, 페이징 없음 */
    public List<StudyCertListItemResponse> selectStudyCertExcelList(ArtappmListRequest request) throws Exception {
        return selectList("artappmManageDAO.selectStudyCertExcelList", request);
    }

    /** 수강확인증 상세 1건 (일자, 내용, 첨부파일). proId+proSeq+reqEsntlId+seq로 지정. 없으면 null */
    public StudyCertDetailResponse selectStudyCertDetail(StudyCertDetailRequest request) throws Exception {
        return selectOne("artappmManageDAO.selectStudyCertDetail", request);
    }

    /** 수강확인증 비움: 해당 fileId에 파일이 없을 때 ARTAPPM.STUDY_CERT 초기화 */
    public int clearStudyCert(ArtappmClearStudyCertParam param) throws Exception {
        return update("artappmManageDAO.clearStudyCert", param);
    }

    /** 변경이력 목록: f_changlist 프로시저 (aGubun=01, proId, proSeq, reqEsntlId) */
    public List<ChangeListItemResponse> selectChangeList(ChangeListRequest request) throws Exception {
        return selectList("artappmManageDAO.selectChangeList", request);
    }

    /** ARTAPMM 전용 REQ_ID 채번 (MAN_0000000000000001 형식) */
    public String getNextArtapmmReqId() throws Exception {
        return selectOne("artappmManageDAO.getNextArtapmmReqId", new EgovMap());
    }

    /** 동일 PRO_ID·PRO_SEQ·REQ_ESNTL_ID 활성(STTUS!=D) 멘토 신청 건수 */
    public int countArtapmmActiveByProSeqReqEsntlId(ArtapmmMentorDuplicateParam param) throws Exception {
        Integer v = selectOne("artappmManageDAO.countArtapmmActiveByProSeqReqEsntlId", param);
        return v != null ? v : 0;
    }

    /** ARTAPMM 멘토 한 건 등록 */
    public int insertArtapmm(ArtapmmMentorSaveParam param) throws Exception {
        return insert("artappmManageDAO.insertArtapmm", param);
    }

    /** 멘토 신청 단건 (REQ_ID만, 사용자웹 MY PAGE) */
    public ArtapmmApplicationListItemResponse selectArtapmmApplicationByReqId(ArtapmmApplicationByReqIdOnlyParam param) throws Exception {
        return selectOne("artappmManageDAO.selectArtapmmApplicationByReqId", param);
    }
}
