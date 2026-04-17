package arami.userWeb.artprom.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.userWeb.artprom.service.dto.request.ArtpromUserDetailRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserFavoriteRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMainCardListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMyAppliedListRequest;
import arami.userWeb.artprom.service.dto.request.ArtpromUserMyFavoriteListRequest;
import arami.userWeb.artprom.service.dto.response.ArtpromMentorWorkProjectItem;
import arami.userWeb.artprom.service.dto.response.ArtpromUserDTO;
import arami.userWeb.artprom.service.dto.response.ArtpromUserListDTO;

/**
 * 지원사업 조회 DAO (사용자웹)
 * ArtpromUser_SQL_mysql.xml (namespace: artpromUserDAO) 사용.
 */
@Repository("artpromUserDAO")
public class ArtpromUserDAO extends EgovAbstractMapper {

    public List<ArtpromUserListDTO> selectArtpromList(ArtpromUserListRequest request) throws Exception {
        return selectList("artpromUserDAO.selectArtpromList", request);
    }

    public int selectArtpromListCount(ArtpromUserListRequest request) throws Exception {
        return selectOne("artpromUserDAO.selectArtpromListCount", request);
    }

    public ArtpromUserDTO selectArtpromDetail(ArtpromUserDetailRequest request) throws Exception {
        return selectOne("artpromUserDAO.selectArtpromDetail", request);
    }

    public List<ArtpromUserListDTO> selectMyAppliedArtpromList(ArtpromUserMyAppliedListRequest request) throws Exception {
        return selectList("artpromUserDAO.selectMyAppliedArtpromList", request);
    }

    public int selectMyAppliedArtpromListCount(ArtpromUserMyAppliedListRequest request) throws Exception {
        return selectOne("artpromUserDAO.selectMyAppliedArtpromListCount", request);
    }

    /** 멘토(ARTAPMM) 기준 내 신청 목록. */
    public List<ArtpromUserListDTO> selectMyAppliedArtpromListMentor(ArtpromUserMyAppliedListRequest request) throws Exception {
        return selectList("artpromUserDAO.selectMyAppliedArtpromListMentor", request);
    }

    public int selectMyAppliedArtpromListMentorCount(ArtpromUserMyAppliedListRequest request) throws Exception {
        return selectOne("artpromUserDAO.selectMyAppliedArtpromListMentorCount", request);
    }

    public List<ArtpromUserListDTO> selectMyFavoriteArtpromList(ArtpromUserMyFavoriteListRequest request) throws Exception {
        return selectList("artpromUserDAO.selectMyFavoriteArtpromList", request);
    }

    public int selectMyFavoriteArtpromListCount(ArtpromUserMyFavoriteListRequest request) throws Exception {
        return selectOne("artpromUserDAO.selectMyFavoriteArtpromListCount", request);
    }

    /** 사용자웹 포털 메인 카드 목록 (REQ_GB 필터 없음, limit만 적용). */
    public List<ArtpromUserListDTO> selectArtpromMainCardList(ArtpromUserMainCardListRequest request) throws Exception {
        return selectList("artpromUserDAO.selectArtpromMainCardList", request);
    }

    /** 멘토업무(mentorWork) 화면 - 사업명 검색용 사업 목록. */
    public List<ArtpromMentorWorkProjectItem> selectMentorWorkProjectList() throws Exception {
        return selectList("artpromUserDAO.selectMentorWorkProjectList", null);
    }

    public int deleteArtmark(ArtpromUserFavoriteRequest request) throws Exception {
        return delete("artpromUserDAO.deleteArtmark", request);
    }

    public int insertArtmark(ArtpromUserFavoriteRequest request) throws Exception {
        return insert("artpromUserDAO.insertArtmark", request);
    }
}
