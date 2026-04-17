package arami.adminWeb.artappm.service;

import java.util.List;
import arami.adminWeb.artappm.service.dto.request.ArtappmDeleteRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmByStudentRequest;
import arami.adminWeb.artappm.service.dto.request.ChangeListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmDetailRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmFileDeleteRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmInsertRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtapmmMentorApplicationRegisterRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmSelectionListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmSelectionUpdateRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmUpdateRequest;
import arami.adminWeb.artappm.service.dto.request.StudyCertDetailRequest;
import arami.adminWeb.artappm.service.dto.response.ArtapmmApplicationListItemResponse;
import arami.adminWeb.artappm.service.dto.response.ArtapmmDuplicateCheckResponse;
import arami.adminWeb.artappm.service.dto.response.ChangeListItemResponse;
import arami.adminWeb.artappm.service.dto.response.ArtappmDTO;
import arami.adminWeb.artappm.service.dto.response.ArtappmResultResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertDetailResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertListItemResponse;
import arami.userWeb.artappm.dto.UserMentorApplicationRegisterRequest;

/**
 * 지원사업 신청 관리 Service 인터페이스
 */
public interface ArtappmManageService {

    List<ArtappmDTO> selectArtappmList(ArtappmListRequest request) throws Exception;
    int selectArtappmListCount(ArtappmListRequest request) throws Exception;
    List<ArtappmDTO> selectArtappmExcelList(ArtappmListRequest request) throws Exception;
    /** 선정관리용 지원사업 신청 목록 (PK 조건만, 페이징 없음) */
    List<ArtappmDTO> selectArtappmSelectionList(ArtappmSelectionListRequest request) throws Exception;
    /** 선정관리: 선정여부(Y/N/R) 일괄 변경 */
    void updateArtappmSelectionBatch(List<ArtappmSelectionUpdateRequest> list, String chgUserId) throws Exception;
    ArtappmDTO selectArtappmDetail(ArtappmDetailRequest request) throws Exception;

    /** 학생·사업별 최신 지원사업 신청 조회 (userWeb bizInput) */
    ArtappmDTO selectArtappmByStudentAndPro(ArtappmByStudentRequest request) throws Exception;

    /** PK(지원사업·회차·신청자) 중복 여부. true면 이미 존재(중복). */
    boolean existsArtappmByPk(ArtappmInsertRequest request) throws Exception;

    /** 지원사업신청ID(REQ_ID) 채번: ARTAPPM MAX(REQ_ID)+1, PRO_ID와 동일 방식 */
    String getNextReqId() throws Exception;

    /**
     * 지원사업 신청 등록. 자격 조건(f_check) 통과 시에만 등록함.
     * @return 자격 미충족 또는 오류 시 반환할 응답 (result 02=자격미충족, 01=시스템오류); 통과 후 등록 성공 시 null
     */
    ArtappmResultResponse insertArtappm(ArtappmInsertRequest request) throws Exception;
    /**
     * 지원사업 신청 수정. 자격 조건(f_check) 통과 시에만 수정함.
     * @return 자격 미충족 또는 오류 시 반환할 응답; 통과 후 수정 성공 시 null
     */
    ArtappmResultResponse updateArtappm(ArtappmUpdateRequest request) throws Exception;
    int deleteArtappm(ArtappmDeleteRequest request) throws Exception;

    /** 첨부파일 1건 삭제: 파일 삭제 후 해당 fileId에 남은 파일이 없으면 ARTAPPM.FILE_ID 비움 */
    void deleteFile(ArtappmFileDeleteRequest request) throws Exception;

    /**
     * 해당 신청 건의 수강확인증 FILE_ID(ARTAPPM.STUDY_CERT) 반환. 없으면 null.
     */
    String getStudyCertFileId(String proId, String proSeq, String reqEsntlId) throws Exception;

    /** REQ_ID로 수강확인증 FILE_ID 반환. 없으면 null. */
    String getStudyCertFileIdByReqId(String reqId) throws Exception;

    /**
     * 수강확인증 등록/추가: 신청 건에 STUDY_CERT가 없으면 ARTAPPM.STUDY_CERT를 newFileId로 UPDATE.
     * 이미 있으면 추가(append)한 경우이므로 UPDATE 하지 않음. (파일 저장은 Controller에서 FileUtil·insertFileInfo 후 호출)
     */
    void uploadStudyCert(String proId, String proSeq, String reqEsntlId, String newFileId) throws Exception;

    /** REQ_ID로 수강확인증 등록/추가. */
    void uploadStudyCertByReqId(String reqId, String newFileId) throws Exception;

    /** 수강확인증 목록 (ARTAPPM + ARTFILE 조인, 한 행 = 수강확인증 파일 1건) */
    List<StudyCertListItemResponse> selectStudyCertList(ArtappmListRequest request) throws Exception;
    int selectStudyCertListCount(ArtappmListRequest request) throws Exception;
    /** 수강확인증 전체 목록 (사용자페이지용, 페이징 없음) */
    List<StudyCertListItemResponse> selectStudyCertListAll(ArtappmListRequest request) throws Exception;
    /** 수강확인증 엑셀 목록 (페이징 없음) */
    List<StudyCertListItemResponse> selectStudyCertExcelList(ArtappmListRequest request) throws Exception;

    /** 수강확인증 상세 (일자=UPLOAD_DTTM, 내용=FILE_DESC, 첨부파일 1건). seq로 특정 파일 지정. 없으면 null */
    StudyCertDetailResponse getStudyCertDetail(StudyCertDetailRequest request) throws Exception;

    /** 수강확인증 1건 삭제: ARTFILE에서 (STUDY_CERT, seq) 삭제. 남은 파일 없으면 ARTAPPM.STUDY_CERT NULL */
    void deleteStudyCert(String proId, String proSeq, String reqEsntlId, Integer seq) throws Exception;

    /** REQ_ID로 수강확인증 1건 삭제. */
    void deleteStudyCertByReqId(String reqId, Integer seq) throws Exception;

    /** 변경이력 목록 (f_changlist 프로시저, aGubun=01) */
    List<ChangeListItemResponse> getChangeList(String proId, String proSeq, String reqEsntlId) throws Exception;

    /**
     * 동일 사업(PRO_ID)·회차(PRO_SEQ)·멘토(REQ_ESNTL_ID)로 활성(STTUS≠D) 신청 건이 있는지.
     * 멘토 신청 등록 전 중복 확인용.
     */
    ArtapmmDuplicateCheckResponse checkMentorApplicationDuplicate(String proId, Integer proSeq, String reqEsntlId) throws Exception;

    /**
     * 멘토 신청 등록 (ARTAPMM 전 컬럼 + 첨부 FILE_ID는 요청에 반영된 뒤 호출).
     * @return result=00이면 성공(응답의 reqId에 신규 REQ_ID), 그 외 코드는 실패
     */
    ArtappmResultResponse registerMentorApplication(String proId, ArtapmmMentorApplicationRegisterRequest request, String chgUserId) throws Exception;

    /**
     * 사용자웹 멘토 신청: {@code REQ_ESNTL_ID}는 로그인 사용자(멘토)로 고정한다.
     * {@code proGb}는 ARTPROM.PRO_GB(08·09)와 반드시 일치해야 하며, 08일 때는 관리자 화면과 동일하게 희망과목·희망 시간대가 필수다.
     */
    ArtappmResultResponse registerMentorApplicationForUser(
            String proId,
            UserMentorApplicationRegisterRequest request,
            String currentEsntlId,
            String userSe,
            String chgUserId) throws Exception;

    /** 멘토 신청 단건 상세 (REQ_ID만, 사용자웹 MY PAGE) */
    ArtapmmApplicationListItemResponse getArtapmmMentorApplicationDetailByReqId(String reqId) throws Exception;

    /** ARTAPPM 상태만 변경 (REQ_ID 기준). 02=REQ_DT, 03=APRR_DT, 04=완료, 11=반려(reaDesc 사용), 12=STOP_DT. reaDesc는 sttusCode=11일 때만 반영, null 가능 */
    void updateArtappmSttusCodeByReqId(String reqId, String sttusCode, String reaDesc) throws Exception;
}
