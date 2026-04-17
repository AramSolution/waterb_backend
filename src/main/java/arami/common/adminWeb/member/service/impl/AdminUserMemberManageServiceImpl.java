package arami.common.adminWeb.member.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import arami.common.CryptoUtil;
import arami.common.adminWeb.member.service.AdminUserMemberManageService;
import arami.common.adminWeb.member.service.AdminUserMemberManageDAO;
import arami.common.adminWeb.member.service.dto.request.MemberListRequest;
import arami.common.adminWeb.member.service.dto.request.MemberDetailRequest;
import arami.common.adminWeb.member.service.dto.request.MemberInsertRequest;
import arami.common.adminWeb.member.service.dto.request.MemberUpdateRequest;
import arami.common.adminWeb.member.service.dto.request.MemberDeleteRequest;
import arami.common.adminWeb.member.service.dto.response.MemberDTO;

@Service("adminUserMemberManageService")
public class AdminUserMemberManageServiceImpl extends EgovAbstractServiceImpl implements AdminUserMemberManageService {

	@Resource(name = "adminUserMemberManageDAO")
	private AdminUserMemberManageDAO adminUserMemberManageDAO;
	
	/** 암호화 유틸리티 */
	@Resource
	private CryptoUtil cryptoUtil;

	// (AJAX / DATATABLE / 리스트 ) : 관리자회원 리스트조회
	@Override
	public List<MemberDTO> selectAdminUserMemberList(MemberListRequest request) throws Exception{
		return adminUserMemberManageDAO.selectAdminUserMemberList(request);
	}

	// (AJAX / DATATABLE / 카운트 ) : 관리자회원 카운트조회
	@Override
	public int selectAdminUserMemberListCount(MemberListRequest request) throws Exception{
		return adminUserMemberManageDAO.selectAdminUserMemberListCount(request);
	}

	// (AJAX / DATATABLE / 리스트 ) : 관리자회원 엑셀 리스트조회
	@Override
	public List<Object> selectAdminUserMemberExcelList(Object object) throws Exception{
		return adminUserMemberManageDAO.selectAdminUserMemberExcelList(object);
	}

	// (AJAX / CRUD / INSERT) : 회원 등록
	@Override
	public int insertAdminMemberAjax(MemberInsertRequest request) throws Exception{
		return adminUserMemberManageDAO.insertAdminMemberAjax(request);
	}

	// (AJAX / CRUD / INSERT) : 권한 등록
	@Override
	public int insertAdminRollAjax(Object object) throws Exception{
		return adminUserMemberManageDAO.insertAdminRollAjax(object);
	}

	// (AJAX / CRUD / SELECT) : 학위정보,경력정보 seq 채번
	@Override
	public int getArmWorkSeq(Object object) throws Exception{
		return adminUserMemberManageDAO.getArmWorkSeq(object);
	}

	// (AJAX / CRUD / INSERT) : 학위정보 등록
	@Override
	public int insertDegreeInfo(Object object) throws Exception{
		return adminUserMemberManageDAO.insertDegreeInfo(object);
	}

	// (AJAX / CRUD / DELETE) : 학위정보 전체 삭제
	@Override
	public int deleteArmworkList(Object object) throws Exception{
		return adminUserMemberManageDAO.deleteArmworkList(object);
	}

	// (AJAX / CRUD / DELETE) : 학위정보 선택 삭제
	@Override
	public int deleteArmworkInfo(Object object) throws Exception{
		return adminUserMemberManageDAO.deleteArmworkInfo(object);
	}

	// (AJAX / CRUD / DELETE) : 자격증정보 전체 삭제
	@Override
	public int deleteLicenceList(Object object) throws Exception{
		return adminUserMemberManageDAO.deleteLicenceList(object);
	}

	// (AJAX / CRUD / DELETE) : 자격증정보 선택 삭제
	@Override
	public int deleteLicenceInfo(Object object) throws Exception{
		return adminUserMemberManageDAO.deleteLicenceInfo(object);
	}

	// (AJAX / CRUD / SELECT) : 자격증정보 seq 채번
	@Override
	public int getArmLiceSeq(Object object) throws Exception{
		return adminUserMemberManageDAO.getArmLiceSeq(object);
	}

	// (AJAX / CRUD / INSERT) : 자격증정보 등록
	@Override
	public int insertLicenceInfo(Object object) throws Exception{
		return adminUserMemberManageDAO.insertLicenceInfo(object);
	}

	// (AJAX / CRUD / INSERT) : 경력정보 등록
	@Override
	public int insertCareerInfo(Object object) throws Exception{
		return adminUserMemberManageDAO.insertCareerInfo(object);
	}

	// (AJAX / DETAIL ) : 회원 상세조회
	@Override
	public MemberDTO selectAdminUserMemberDetail(MemberDetailRequest request) throws Exception{
		return adminUserMemberManageDAO.selectAdminUserMemberDetail(request);
	}

	// (AJAX / DETAIL ) : 회원 권한 상세조회
	@Override
	public Object selectAuthInfoDetail(Object object) throws Exception{
		return adminUserMemberManageDAO.selectAuthInfoDetail(object);
	}

	// (AJAX / DETAIL ) : 학위정보
	@Override
	public List<Object> selectDegreeInfoDetail(Object object) throws Exception{
		return adminUserMemberManageDAO.selectDegreeInfoDetail(object);
	}

	// (AJAX / DETAIL ) : 자격증정보
	@Override
	public List<Object> selectLicenceInfoDetail(Object object) throws Exception{
		return adminUserMemberManageDAO.selectLicenceInfoDetail(object);
	}

	// (AJAX / DETAIL ) : 경력정보
	@Override
	public List<Object> selectCareerInfoDetail(Object object) throws Exception{
		return adminUserMemberManageDAO.selectCareerInfoDetail(object);
	}

	// (AJAX / CRUD / UPDAETE) : 회원 수정
	@Override
	public int updateAdminUserMember(MemberUpdateRequest request) throws Exception{
		return adminUserMemberManageDAO.updateAdminUserMember(request);
	}

	// (AJAX / CRUD / UPDATE) : 회원 권한 수정
	@Override
	public int updateAdminRollAjax(Object object) throws Exception{
		return adminUserMemberManageDAO.updateAdminRollAjax(object);
	}

	// (AJAX / DATATABLE / 리스트 ) : 관리자회원 휴가일수 조회
	@Override
	public List<Object> selectVacaDayInfoList(Object object) throws Exception{
		return adminUserMemberManageDAO.selectVacaDayInfoList(object);
	}

	// (AJAX / DATATABLE / 카운트 ) : 관리자회원 휴가일수 개수 조회
	@Override
	public int selectVacaDayInfoListCount(Object object) throws Exception{
		return adminUserMemberManageDAO.selectVacaDayInfoListCount(object);
	}

	// (AJAX / CRUD / DELETE) : 관리자회원 휴가일수 삭제
	@Override
	public int deleteVacaDayInfo(Object object) throws Exception{
		return adminUserMemberManageDAO.deleteVacaDayInfo(object);
	}

	// (AJAX / CRUD / DELETE) : 관리자회원 휴가일수 리스트 삭제
	@Override
	public int deleteVacaDayInfoList(Object object) throws Exception{
		return adminUserMemberManageDAO.deleteVacaDayInfoList(object);
	}

	// (AJAX / CRUD / INSERT) : 관리자회원 휴가일수 등록
	@Override
	public int insertVacaDayInfoList(Object object) throws Exception{
		return adminUserMemberManageDAO.insertVacaDayInfoList(object);
	}

	// (AJAX / CRUD / UPDAETE) : 관리자회원 휴가일수 수정
	@Override
	public int updateVacaDayInfoList(Object object) throws Exception{
		return adminUserMemberManageDAO.updateVacaDayInfoList(object);
	}

	// (AJAX / CRUD / UPDAETE) : 관리자회원 비밀번호 수정
	@Override
	public int updateAdminUserPw(Object object) throws Exception{
		return adminUserMemberManageDAO.updateAdminUserPw(object);
	}

	// (AJAX / CRUD / UPDAETE) : 게시글 관리자 수정
	@Override
	public int updateSetBrdManager(Object object) throws Exception{
		return adminUserMemberManageDAO.updateSetBrdManager(object);
	}

	@Override
	public List<Object> selectAuthList(Object object) throws Exception{
		return adminUserMemberManageDAO.selectAuthList(object);
	}

	@Override
	public int deleteUserMemberAjax(MemberDeleteRequest request) throws Exception{
		return adminUserMemberManageDAO.deleteUserMemberAjax(request);
	}
}

