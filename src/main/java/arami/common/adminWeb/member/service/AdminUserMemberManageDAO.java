package arami.common.adminWeb.member.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.common.adminWeb.member.service.dto.request.MemberListRequest;
import arami.common.adminWeb.member.service.dto.request.MemberDetailRequest;
import arami.common.adminWeb.member.service.dto.request.MemberInsertRequest;
import arami.common.adminWeb.member.service.dto.request.MemberUpdateRequest;
import arami.common.adminWeb.member.service.dto.request.MemberDeleteRequest;
import arami.common.adminWeb.member.service.dto.response.MemberDTO;

@Repository("adminUserMemberManageDAO")
public class AdminUserMemberManageDAO extends EgovAbstractMapper {

	// (AJAX / DATATABLE / 리스트 ) : 관리자회원 리스트조회
	public List<MemberDTO> selectAdminUserMemberList(MemberListRequest request) throws Exception{
		return selectList("adminUserMemberManageDAO.selectAdminUserMemberList", request);
	}

	// (AJAX / DATATABLE / 카운트 ) : 관리자회원 카운트조회
	public int selectAdminUserMemberListCount(MemberListRequest request) throws Exception{
		return selectOne("adminUserMemberManageDAO.selectAdminUserMemberListCount", request);
	}

	// (AJAX / DATATABLE / 리스트 ) : 관리자회원 엑셀 리스트조회
	public List<Object> selectAdminUserMemberExcelList(Object object) throws Exception{
		return selectList("adminUserMemberManageDAO.selectAdminUserMemberExcelList", object);
	}

	// (AJAX / CRUD / INSERT) : 회원 등록
	public int insertAdminMemberAjax(MemberInsertRequest request) throws Exception{
		return insert("adminUserMemberManageDAO.insertAdminMemberAjax", request);
	}

	// (AJAX / CRUD / INSERT) : 권한 등록
	public int insertAdminRollAjax(Object object) throws Exception{
		return insert("adminUserMemberManageDAO.insertAdminRollAjax", object);
	}

	// (AJAX / CRUD / SELECT) : 학위정보,경력정보 seq 채번
	public int getArmWorkSeq(Object object) throws Exception{
		return selectOne("adminUserMemberManageDAO.getArmWorkSeq", object);
	}

	// (AJAX / CRUD / INSERT) : 학위정보 등록
	public int insertDegreeInfo(Object object) throws Exception{
		return insert("adminUserMemberManageDAO.insertDegreeInfo", object);
	}

	// (AJAX / CRUD / DELETE) : 학위정보 전체 삭제
	public int deleteArmworkList(Object object) throws Exception{
		return delete("adminUserMemberManageDAO.deleteArmworkList", object);
	}

	// (AJAX / CRUD / DELETE) : 학위정보 선택 삭제
	public int deleteArmworkInfo(Object object) throws Exception{
		return delete("adminUserMemberManageDAO.deleteArmworkInfo", object);
	}

	// (AJAX / CRUD / DELETE) : 자격증정보 전체 삭제
	public int deleteLicenceList(Object object) throws Exception{
		return delete("adminUserMemberManageDAO.deleteLicenceList", object);
	}

	// (AJAX / CRUD / DELETE) : 자격증정보 선택 삭제
	public int deleteLicenceInfo(Object object) throws Exception{
		return delete("adminUserMemberManageDAO.deleteLicenceInfo", object);
	}

	// (AJAX / CRUD / SELECT) : 자격증정보 seq 채번
	public int getArmLiceSeq(Object object) throws Exception{
		return selectOne("adminUserMemberManageDAO.getArmLiceSeq", object);
	}

	// (AJAX / CRUD / INSERT) : 자격증정보 등록
	public int insertLicenceInfo(Object object) throws Exception{
		return insert("adminUserMemberManageDAO.insertLicenceInfo", object);
	}

	// (AJAX / CRUD / INSERT) : 경력정보 등록
	public int insertCareerInfo(Object object) throws Exception{
		return insert("adminUserMemberManageDAO.insertCareerInfo", object);
	}

	// (AJAX / DETAIL ) : 회원 상세조회
	public MemberDTO selectAdminUserMemberDetail(MemberDetailRequest request) throws Exception{
		return selectOne("adminUserMemberManageDAO.selectAdminUserMemberDetail", request);
	}

	// (AJAX / DETAI ) : 회원 권한 상세조회
	public Object selectAuthInfoDetail(Object object) throws Exception{
		return selectOne("adminUserMemberManageDAO.selectAuthInfoDetail", object);
	}

	// (AJAX / DETAIL ) : 학위정보 상세조회
	public List<Object> selectDegreeInfoDetail(Object object) throws Exception{
		return selectList("adminUserMemberManageDAO.selectDegreeInfoDetail", object);
	}

	// (AJAX / DETAIL ) : 자격증정보 상세조회
	public List<Object> selectLicenceInfoDetail(Object object) throws Exception{
		return selectList("adminUserMemberManageDAO.selectLicenceInfoDetail", object);
	}

	// (AJAX / DETAIL ) : 경력정보 상세조회
	public List<Object> selectCareerInfoDetail(Object object) throws Exception{
		return selectList("adminUserMemberManageDAO.selectCareerInfoDetail", object);
	}

	// (AJAX / CRUD / UPDAETE) : 회원 수정
	public int updateAdminUserMember(MemberUpdateRequest request) throws Exception{
		return update("adminUserMemberManageDAO.updateAdminUserMember", request);
	}

	// (AJAX / CRUD / UPDATE) : 회원 권한 수정
	public int updateAdminRollAjax(Object object) throws Exception{
		return update("adminUserMemberManageDAO.updateAdminRollAjax", object);
	}

	// (AJAX / DATATABLE / 리스트 ) : 관리자회원 휴가일수 조회
	public List<Object> selectVacaDayInfoList(Object object) throws Exception{
		return selectList("adminUserMemberManageDAO.selectVacaDayInfoList", object);
	}

	// (AJAX / DATATABLE / 카운트 ) : 관리자회원 휴가일수 개수 조회
	public int selectVacaDayInfoListCount(Object object) throws Exception{
		return selectOne("adminUserMemberManageDAO.selectVacaDayInfoListCount", object);
	}

	// (AJAX / CRUD / DELETE) : 관리자회원 휴가일수 삭제
	public int deleteVacaDayInfo(Object object) throws Exception{
		return delete("adminUserMemberManageDAO.deleteVacaDayInfo", object);
	}

	// (AJAX / CRUD / DELETE) : 관리자회원 휴가일수 리스트 삭제
	public int deleteVacaDayInfoList(Object object) throws Exception{
		return delete("adminUserMemberManageDAO.deleteVacaDayInfoList", object);
	}

	// (AJAX / CRUD / INSERT) : 관리자회원 휴가일수 등록
	public int insertVacaDayInfoList(Object object) throws Exception{
		return update("adminUserMemberManageDAO.insertVacaDayInfoList", object);
	}

	// (AJAX / CRUD / UPDAETE) : 관리자회원 휴가일수 수정
	public int updateVacaDayInfoList(Object object) throws Exception{
		return update("adminUserMemberManageDAO.updateVacaDayInfoList", object);
	}

	// (AJAX / CRUD / UPDAETE) : 관리자회원 비밀번호 수정
	public int updateAdminUserPw(Object object) throws Exception{
		return update("adminUserMemberManageDAO.updateAdminUserPw", object);
	}

	// (AJAX / CRUD / UPDAETE) : 게시글 관리자 수정
	public int updateSetBrdManager(Object object) throws Exception{
		return update("adminUserMemberManageDAO.updateSetBrdManager", object);
	}

	public List<Object> selectAuthList(Object object) throws Exception{
		return selectList("adminUserMemberManageDAO.selectAuthList", object);
	}

	public int deleteUserMemberAjax(MemberDeleteRequest request) throws Exception{
		return update("adminUserMemberManageDAO.deleteUserMemberAjax", request);
	}
}
