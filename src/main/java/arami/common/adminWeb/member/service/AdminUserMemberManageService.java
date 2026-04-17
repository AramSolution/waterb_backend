package arami.common.adminWeb.member.service;

import java.util.List;

import arami.common.adminWeb.member.service.dto.request.MemberListRequest;
import arami.common.adminWeb.member.service.dto.request.MemberDetailRequest;
import arami.common.adminWeb.member.service.dto.request.MemberInsertRequest;
import arami.common.adminWeb.member.service.dto.request.MemberUpdateRequest;
import arami.common.adminWeb.member.service.dto.request.MemberDeleteRequest;
import arami.common.adminWeb.member.service.dto.response.MemberDTO;

public interface AdminUserMemberManageService {

    /**
	 * (AJAX / DATATABLE / 리스트 ) : 관리자회원 리스트조회
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public List<MemberDTO> selectAdminUserMemberList(MemberListRequest request) throws Exception;

	/**
	 * (AJAX / DATATABLE / 카운트 ) : 관리자회원 카운트조회
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public int selectAdminUserMemberListCount(MemberListRequest request) throws Exception;

	/**
	 * (AJAX / DATATABLE / 리스트 ) : 관리자회원 엑셀 리스트조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectAdminUserMemberExcelList(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / INSERT) : 회원 회원 등록
	 * @param  request
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int insertAdminMemberAjax(MemberInsertRequest request) throws Exception;

	/**
	 * (AJAX / CRUD / INSERT) : 권한 등록
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int insertAdminRollAjax(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / SELECT) : 학위정보,경력정보 seq 채번
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int getArmWorkSeq(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / INSERT) : 학위정보 등록
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int insertDegreeInfo(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / DELETE) : 학위정보 전체 삭제
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int deleteArmworkList(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / DELETE) : 학위정보 선택 삭제
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int deleteArmworkInfo(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / DELETE) : 자격증정보 전체 삭제
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int deleteLicenceList(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / DELETE) : 자격증정보 선택 삭제
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int deleteLicenceInfo(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / SELECT) : 자격증정보 seq 채번
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int getArmLiceSeq(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / INSERT) : 자격증정보 등록
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int insertLicenceInfo(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / INSERT) : 경력정보 등록
	 * @param  object
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int insertCareerInfo(Object object) throws Exception;

	/**
	 * (AJAX / DETAIL) : 회원 상세조회
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public MemberDTO selectAdminUserMemberDetail(MemberDetailRequest request) throws Exception;

	/**
	 * (AJAX / DETAIL) : 회원 권한 상세조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object selectAuthInfoDetail(Object object) throws Exception;

	/**
	 * (AJAX / DETAIL) : 학위정보 상세조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectDegreeInfoDetail(Object object) throws Exception;

	/**
	 * (AJAX / DETAIL) : 자격증정보 상세조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectLicenceInfoDetail(Object object) throws Exception;

	/**
	 * (AJAX / DETAIL) : 경력정보 상세조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCareerInfoDetail(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / UPDAETE) : 회원 수정
	 * @param  request ( 회원수정정보 )
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int updateAdminUserMember(MemberUpdateRequest request) throws Exception;

	/**
	 * (AJAX / CRUD / UPDATE) : 회원 권한 수정
	 * @param  object ( 회원 코드 )
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int updateAdminRollAjax(Object object) throws Exception;

	/**
	 * (AJAX / DATATABLE / 리스트 ) : 관리자회원 휴가일수 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectVacaDayInfoList(Object object) throws Exception;

	/**
	 * (AJAX / DATATABLE / 카운트 ) : 관리자회원 휴가일수 개수 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int selectVacaDayInfoListCount(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / DELETE) : 관리자회원 휴가일수 삭제
	 */
	public int deleteVacaDayInfo(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / DELETE) : 관리자회원 휴가일수 리스트 삭제
	 */
	public int deleteVacaDayInfoList(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / INSERT) : 관리자회원 휴가일수 등록
	 */
	public int insertVacaDayInfoList(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / UPDAETE) : 관리자회원 휴가일수 수정
	 */
	public int updateVacaDayInfoList(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / UPDAETE) : 관리자회원 비밀번호 수정
	 */
	public int updateAdminUserPw(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / UPDAETE) : 게시글 관리자 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int updateSetBrdManager(Object object) throws Exception;

	public List<Object> selectAuthList(Object object) throws Exception;

	/**
	 * (AJAX / CRUD / DELETE) : 관리자회원 탈퇴
	 * @param request
	 * @return 1, 0 ( 성공여부 )
	 * @throws Exception
	 */
	public int deleteUserMemberAjax(MemberDeleteRequest request) throws Exception;
}
