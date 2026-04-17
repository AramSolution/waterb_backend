package arami.userWeb.artprom.service;

import java.util.List;

import arami.userWeb.artprom.service.dto.request.ArtpromUserDetailRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserFavoriteRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMainCardListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMyAppliedListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMyFavoriteListRequest;
import arami.userWeb.artprom.service.dto.response.ArtpromMentorWorkProjectItem;
import arami.userWeb.artprom.service.dto.response.ArtpromUserDetailResponse;
import arami.userWeb.artprom.service.dto.response.ArtpromUserFavoriteResponse;
import arami.userWeb.artprom.service.dto.response.ArtpromUserListDTO;

/**
 * 지원사업 조회 Service 인터페이스 (사용자웹)
 * 목록/상세 조회만 제공.
 */
public interface ArtpromUserService {

    List<ArtpromUserListDTO> selectArtpromList(ArtpromUserListRequest request) throws Exception;

    int selectArtpromListCount(ArtpromUserListRequest request) throws Exception;

    ArtpromUserDetailResponse selectArtpromDetailResponse(ArtpromUserDetailRequest request) throws Exception;

    /**
     * 로그인 사용자가 임시저장/신청한 지원사업 목록 (MY PAGE 신청현황).
     * 비로그인 시 빈 목록 반환.
     */
    List<ArtpromUserListDTO> selectMyAppliedArtpromList(ArtpromUserMyAppliedListRequest request) throws Exception;

    int selectMyAppliedArtpromListCount(ArtpromUserMyAppliedListRequest request) throws Exception;

    /**
     * 멘토(MNR) 로그인 사용자가 임시저장/신청한 지원사업 목록 (ARTAPMM, MY PAGE 멘토 신청현황).
     * 멘토가 아니거나 비로그인 시 빈 목록.
     */
    List<ArtpromUserListDTO> selectMyAppliedArtpromListMentor(ArtpromUserMyAppliedListRequest request) throws Exception;

    int selectMyAppliedArtpromListMentorCount(ArtpromUserMyAppliedListRequest request) throws Exception;

    /** 로그인 사용자가 즐겨찾기한 지원사업 목록 (MY PAGE 즐겨찾기). */
    List<ArtpromUserListDTO> selectMyFavoriteArtpromList(ArtpromUserMyFavoriteListRequest request) throws Exception;

    int selectMyFavoriteArtpromListCount(ArtpromUserMyFavoriteListRequest request) throws Exception;

    /** 사용자웹 포털(/userWeb) 메인 카드 목록 (REQ_GB 필터 없음). */
    List<ArtpromUserListDTO> selectArtpromMainCardList(ArtpromUserMainCardListRequest request) throws Exception;

    /** 멘토업무(mentorWork) 화면 - 사업명 검색 버튼용 사업 목록. 진행/완료(RUN_STA 04·05)만. */
    List<ArtpromMentorWorkProjectItem> getMentorWorkProjectList() throws Exception;

    /**
     * 사용자 즐겨찾기 저장(동일 키 삭제 후 재등록).
     */
    ArtpromUserFavoriteResponse saveFavoriteArtprom(ArtpromUserFavoriteRequest request) throws Exception;

    /**
     * 사용자 즐겨찾기 삭제.
     */
    ArtpromUserFavoriteResponse deleteFavoriteArtprom(String proId) throws Exception;
}
