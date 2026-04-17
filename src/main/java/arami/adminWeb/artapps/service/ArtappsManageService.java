package arami.adminWeb.artapps.service;

import java.util.List;

import arami.adminWeb.artapps.service.dto.request.ArtappsApplicationListRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsDetailRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsInsertRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsListRequest;
import arami.adminWeb.artapps.service.dto.request.ArtappsUpdateRequest;
import arami.adminWeb.artapps.service.dto.response.ArtappsApplicationListRowDTO;
import arami.adminWeb.artapps.service.dto.response.ArtappsDTO;
import arami.adminWeb.artapps.service.dto.response.ArtappsDetailResponse;
import arami.adminWeb.artapps.service.dto.response.ArtappsResultResponse;

public interface ArtappsManageService {

    /** 목록/건수/엑셀/상세/채번/수정/삭제/파일삭제: {@code artpromManageDAO} (ArtpromManageService 경유). */

    List<ArtappsDTO> selectList(ArtappsListRequest request) throws Exception;

    int selectListCount(ArtappsListRequest request) throws Exception;

    List<ArtappsDTO> selectExcelList(ArtappsListRequest request) throws Exception;

    ArtappsDetailResponse selectDetail(String proId) throws Exception;

    ArtappsDetailResponse selectDetailResponse(ArtappsDetailRequest request) throws Exception;

    String getNextProId() throws Exception;

    /**
     * 공부의명수(ARTAPPS) 신청 등록. 첨부는 컨트롤러에서 저장 후 {@code fileId} 등이 채워진 상태로 호출.
     */
    ArtappsResultResponse insertArtapps(ArtappsInsertRequest request) throws Exception;

    /**
     * 공부의명수(ARTAPPS) 신청 수정. 첨부는 컨트롤러에서 저장 후 호출.
     */
    ArtappsResultResponse updateArtapps(ArtappsUpdateRequest request) throws Exception;

    ArtappsResultResponse delete(String proId, String uniqId) throws Exception;

    ArtappsResultResponse deleteProFile(String proId, Long fileId, Integer seq) throws Exception;

    ArtappsResultResponse deleteFile(String proId, Long fileId, Integer seq) throws Exception;

    /** {@code artappsManageDAO.countArtappsByProIdProSeq} */
    int countArtappsByProIdProSeq(ArtappsInsertRequest request) throws Exception;

    /**
     * 지원사업신청ID(REQ_ID) 기준 지원사업 신청(ARTAPPM) 및 공부의 명수(ARTAPPS) 행 삭제.
     */
    ArtappsResultResponse deleteApplicationsByReqId(String reqId) throws Exception;

    /** REQ_ID 기준 ARTAPPM + ARTAPPS 상태 코드 동시 변경 */
    void updateArtappsSttusCodeByReqId(String reqId, String sttusCode, String reaDesc) throws Exception;

    /**
     * 공부의 명수 신청목록 건수 (ARTAPPM INNER JOIN ARTAPPS ON REQ_ID).
     */
    int countArtappsApplicationList(ArtappsApplicationListRequest request) throws Exception;

    /**
     * 공부의 명수 신청목록 (ARTAPPM INNER JOIN ARTAPPS ON REQ_ID). 페이징은 {@link ArtappsApplicationListRequest#setDefaultPaging()} 후 DAO.
     */
    List<ArtappsApplicationListRowDTO> selectArtappsApplicationList(ArtappsApplicationListRequest request)
            throws Exception;
}

