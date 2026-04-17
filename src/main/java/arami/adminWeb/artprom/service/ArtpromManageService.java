package arami.adminWeb.artprom.service;

import java.util.List;

import arami.adminWeb.artprom.service.dto.request.ArtpromDetailRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromFileDeleteRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromInsertRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromListRequest;
import arami.adminWeb.artprom.service.dto.request.ArtpromUpdateRequest;
import arami.adminWeb.artprom.service.dto.response.ArtprodScheduleItemResponse;
import arami.adminWeb.artprom.service.dto.response.ArtpromDTO;
import arami.adminWeb.artprom.service.dto.response.ArtpromDetailResponse;

/**
 * 지원사업 관리 Service 인터페이스
 */
public interface ArtpromManageService {

    List<ArtpromDTO> selectArtpromList(ArtpromListRequest request) throws Exception;

    int selectArtpromListCount(ArtpromListRequest request) throws Exception;

    List<ArtpromDTO> selectArtpromExcelList(ArtpromListRequest request) throws Exception;

    ArtpromDTO selectArtpromDetail(ArtpromDetailRequest request) throws Exception;

    /** 상세 + 홍보파일 목록 + 첨부파일 목록을 채운 응답 반환 (Controller는 result만 설정) */
    ArtpromDetailResponse selectArtpromDetailResponse(ArtpromDetailRequest request) throws Exception;

    String getNextProId() throws Exception;

    int insertArtprom(ArtpromInsertRequest request) throws Exception;

    int updateArtprom(ArtpromUpdateRequest request) throws Exception;

    int deleteArtprom(ArtpromDetailRequest request) throws Exception;

    /** 홍보파일 그룹 비움: 해당 proId의 ARTPROM.PRO_FILE_ID를 ''로 갱신 */
    int clearProFileId(String proId) throws Exception;

    /** 첨부파일 그룹 비움: 해당 proId의 ARTPROM.FILE_ID를 ''로 갱신 */
    int clearFileId(String proId) throws Exception;

    /** 홍보파일 1건 삭제: 파일 삭제 후 해당 fileId에 남은 파일이 없으면 PRO_FILE_ID 비움 */
    void deleteProFile(ArtpromFileDeleteRequest request) throws Exception;

    /** 첨부파일 1건 삭제: 파일 삭제 후 해당 fileId에 남은 파일이 없으면 FILE_ID 비움 */
    void deleteFile(ArtpromFileDeleteRequest request) throws Exception;

    /** PRO_ID 기준 ARTPROD 일정 + 신청 인원 목록 (사용자웹 등에서 사용) */
    List<ArtprodScheduleItemResponse> getScheduleListWithApplyCnt(String proId) throws Exception;
}
