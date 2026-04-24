package arami.common.adminWeb.code.service;

import java.util.List;

import arami.common.adminWeb.code.service.dto.response.BuildingUseCodeTreeResponse;
import arami.common.adminWeb.code.service.dto.response.DetailCodeListByCategoryResponse;
import arami.common.adminWeb.code.service.dto.response.DetailCodeResponse;

public interface CmmCodeManageService {

	/**
	 * 공통분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectClCodeList(Object object) throws Exception;

	/**
	 * 대분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCmmCodeList(Object object) throws Exception;
	
	/**
	 * 대분류코드 목록 개수 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int selectCmmCodeCount(Object object) throws Exception;
	
	/**
	 * 대분류코드 엑셀 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCmmCodeExcelList(Object object) throws Exception;

	/**
	 * 대분류코드 중복 체크
	 * @param object
	 * @return Y : 가능, N : 불가
	 * @throws Exception
	 */
	public String checkCmmCodeDuplication(Object object) throws Exception;

	/**
	 * 대분류코드 등록
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object insertCmmCode(Object object) throws Exception;

	/**
	 * 대분류코드 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object selectCmmCodeDetail(Object object) throws Exception;

	/**
	 * 대분류코드 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object updateCmmCode(Object object) throws Exception;

	/**
	 * 대분류코드 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object deleteCmmCode(Object object) throws Exception;

	/**
	 * 소분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCmmDetailCodeList(Object object) throws Exception;
	
	/**
	 * 소분류코드 목록 개수 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int selectCmmDetailCodeCount(Object object) throws Exception;
	
	/**
	 * 소분류코드 엑셀 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCmmDetailCodeExcelList(Object object) throws Exception;

	/**
	 * 코드ID 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCodeIdList(Object object) throws Exception;

	/**
	 * 소분류코드 리스트 조회 (USE_AT='Y', codeId 기준)
	 * @param object egovMap 등 codeId 포함
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectLetDetailCodeList(Object object) throws Exception;

	/**
	 * 소분류코드 리스트 조회 (REST API용, DTO 반환)
	 * @param codeId 코드 ID (필수)
	 * @return 소분류코드 목록 (USE_AT='Y', ORDER_BY 정렬)
	 */
	List<DetailCodeResponse> getDetailCodeListByCodeId(String codeId);

	/**
	 * 소분류코드 리스트 조회 (학교/학생 구분별 카테고리 매핑, USE_AT='Y')
	 *
	 * @param codeId       대분류 코드 ID (필수)
	 * @param studentCode  중학교(M) / 고등학교(H) 구분, 없으면 null (해당 학년대 전용 코드 제외)
	 */
	List<DetailCodeListByCategoryResponse> getDetailCodeListByCategory(String codeId, String studentCode);

	/**
	 * 건축용도 코드 트리 (중분류별 code/name + 소분류 children, 요청 파라미터 없음)
	 */
	List<BuildingUseCodeTreeResponse> getBuildingUseCodeList();

	/**
	 * 소분류코드 중복 체크
	 * @param object
	 * @return Y : 가능 N : 불가
	 * @throws Exception
	 */
	public String checkCmmDetailCodeDuplication(Object object) throws Exception;

	/**
	 * 소분류코드 등록
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object insertCmmDetailCode(Object object) throws Exception;

	/**
	 * 소분류코드 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object selectCmmDetailCodeDetail(Object object) throws Exception;

	/**
	 * 소분류코드 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object updateCmmDetailCode(Object object) throws Exception;

	/**
	 * 소분류코드 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object deleteCmmDetailCode(Object object) throws Exception;

}
