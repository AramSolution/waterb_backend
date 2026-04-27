package arami.common.adminWeb.code.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.common.adminWeb.code.service.dto.request.DetailCodeListByCategoryRequest;
import arami.common.adminWeb.code.service.dto.request.DetailCodeListRequest;
import arami.common.adminWeb.code.service.dto.request.BuildingUseCodeUnitPriceRequest;
import arami.common.adminWeb.code.service.dto.response.BuildingUseCodeResponse;
import arami.common.adminWeb.code.service.dto.response.DetailCodeListByCategoryResponse;
import arami.common.adminWeb.code.service.dto.response.DetailCodeResponse;

@Repository("CmmCodeManageDAO")
public class CmmCodeManageDAO extends EgovAbstractMapper {

	/**
	 * 공통분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectClCodeList(Object object) throws Exception {
		return selectList("CmmCodeManageDAO.selectClCodeList", object);
	}

	/**
	 * 대분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCmmCodeList(Object object) throws Exception {
		return selectList("CmmCodeManageDAO.selectCmmCodeList", object);
	}
	
	/**
	 * 대분류코드 목록 개수 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int selectCmmCodeCount(Object object) throws Exception {
		return selectOne("CmmCodeManageDAO.selectCmmCodeCount", object);
	}
	
	/**
	 * 대분류코드 엑셀 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCmmCodeExcelList(Object object) throws Exception {
		return selectList("CmmCodeManageDAO.selectCmmCodeExcelList", object);
	}

	/**
	 * 대분류코드 중복 체크
	 * @param object
	 * @return Y : 가능, N : 불가
	 * @throws Exception
	 */
	public String checkCmmCodeDuplication(Object object) throws Exception{
    	return selectOne("CmmCodeManageDAO.checkCmmCodeDuplication", object);
    }

	/**
	 * 대분류코드 등록
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object insertCmmCode(Object object) throws Exception {
		return insert("CmmCodeManageDAO.insertCmmCode", object);
	}

	/**
	 * 대분류코드 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object selectCmmCodeDetail(Object object) throws Exception {
		return selectOne("CmmCodeManageDAO.selectCmmCodeDetail", object);
	}

	/**
	 * 대분류코드 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object updateCmmCode(Object object) throws Exception {
		return update("CmmCodeManageDAO.updateCmmCode", object);
	}

	/**
	 * 대분류코드 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object deleteCmmCode(Object object) throws Exception {
		return update("CmmCodeManageDAO.deleteCmmCode", object);
	}

	/**
	 * 소분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCmmDetailCodeList(Object object) throws Exception {
		return selectList("CmmCodeManageDAO.selectCmmDetailCodeList", object);
	}
	
	/**
	 * 소분류코드 목록 개수 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int selectCmmDetailCodeCount(Object object) throws Exception {
		return selectOne("CmmCodeManageDAO.selectCmmDetailCodeCount", object);
	}
	
	/**
	 * 소분류코드 엑셀 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCmmDetailCodeExcelList(Object object) throws Exception {
		return selectList("CmmCodeManageDAO.selectCmmDetailCodeExcelList", object);
	}

	/**
	 * 코드ID 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectCodeIdList(Object object) throws Exception {
		return selectList("CmmCodeManageDAO.selectCodeIdList", object);
	}

	/**
	 * 소분류코드 중복 체크
	 * @param object
	 * @return Y : 가능 N : 불가
	 * @throws Exception
	 */
	public String checkCmmDetailCodeDuplication(Object object) throws Exception {
		return selectOne("CmmCodeManageDAO.checkCmmDetailCodeDuplication", object);
	}

	/**
	 * 소분류코드 등록
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object insertCmmDetailCode(Object object) throws Exception {
		return insert("CmmCodeManageDAO.insertCmmDetailCode", object);
	}

	/**
	 * 소분류코드 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object selectCmmDetailCodeDetail(Object object) throws Exception {
		return selectOne("CmmCodeManageDAO.selectCmmDetailCodeDetail", object);
	}

	/**	
	 * 소분류코드 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object updateCmmDetailCode(Object object) throws Exception {
		return update("CmmCodeManageDAO.updateCmmDetailCode", object);
	}

	/**
	 * 소분류코드 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object deleteCmmDetailCode(Object object) throws Exception {
		return update("CmmCodeManageDAO.deleteCmmDetailCode", object);
	}

	/**
	 * 소분류코드 리스트 조회 (USE_AT='Y', codeId 기준 ORDER_BY 정렬)
	 * @param object egovMap 등 codeId 포함
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectLetDetailCodeList(Object object) throws Exception {
		return selectList("CmmCodeManageDAO.selectLetDetailCodeList", object);
	}

	/**
	 * 소분류코드 리스트 조회 (REST API용: DTO 파라미터/결과)
	 * @param request codeId 포함
	 * @return DetailCodeResponse 목록
	 */
	public List<DetailCodeResponse> selectLetDetailCodeListDto(DetailCodeListRequest request) {
		return selectList("CmmCodeManageDAO.selectLetDetailCodeListDto", request);
	}

	/**
	 * 소분류코드 리스트 조회 (학교/학생 구분별 카테고리 매핑, REST API용)
	 */
	public List<DetailCodeListByCategoryResponse> selectLetDetailCodeListByCategory(DetailCodeListByCategoryRequest request) {
		return selectList("CmmCodeManageDAO.selectLetDetailCodeListByCategory", request);
	}

	/**
	 * 건축용도 코드 리스트 (WAT001 × WAT002, 바인드 파라미터 없음)
	 */
	public List<BuildingUseCodeResponse> selectBuildingUseCodeList() {
		return selectList("CmmCodeManageDAO.selectBuildingUseCodeList", null);
	}

	/**
	 * 건축용도 코드 구분별 기준단가 조회
	 */
	public List<DetailCodeResponse> selectBuildingUseCodeUnitPrice(BuildingUseCodeUnitPriceRequest request) {
		return selectList("CmmCodeManageDAO.selectBuildingUseCodeUnitPrice", request);
	}

}