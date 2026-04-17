package arami.common.adminWeb.code.service.impl;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import arami.common.adminWeb.code.service.CmmCodeManageDAO;
import arami.common.adminWeb.code.service.CmmCodeManageService;
import arami.common.adminWeb.code.service.dto.request.DetailCodeListByCategoryRequest;
import arami.common.adminWeb.code.service.dto.request.DetailCodeListRequest;
import arami.common.adminWeb.code.service.dto.response.DetailCodeListByCategoryResponse;
import arami.common.adminWeb.code.service.dto.response.DetailCodeResponse;
import arami.common.error.BusinessException;
import arami.common.error.ErrorCode;

@Service("CmmCodeManageService")
public class CmmCodeManageServiceImpl extends EgovAbstractServiceImpl implements CmmCodeManageService {

	@Resource(name = "CmmCodeManageDAO")
	private CmmCodeManageDAO cmmCodeManageDAO;

	/**
	 * 공통분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public List<Object> selectClCodeList(Object object) throws Exception {
    	return cmmCodeManageDAO.selectClCodeList(object);
    }
	
	/**
	 * 대분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public List<Object> selectCmmCodeList(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCmmCodeList(object);
    }
    
    @Override
    public int selectCmmCodeCount(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCmmCodeCount(object);
    }
    
    @Override
    public List<Object> selectCmmCodeExcelList(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCmmCodeExcelList(object);
    }

	/**
	 * 대분류코드 중복 체크
	 * @param object
	 * @return Y : 가능, N : 불가
	 * @throws Exception
	 */
	@Override
    public String checkCmmCodeDuplication(Object object) throws Exception {
    	return cmmCodeManageDAO.checkCmmCodeDuplication(object);
    }

	/**
	 * 대분류코드 등록
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object insertCmmCode(Object object) throws Exception {
    	return cmmCodeManageDAO.insertCmmCode(object);
    }

	/**
	 * 대분류코드 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object selectCmmCodeDetail(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCmmCodeDetail(object);
    }

	/**
	 * 대분류코드 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object updateCmmCode(Object object) throws Exception {
    	return cmmCodeManageDAO.updateCmmCode(object);
    }

	/**
	 * 대분류코드 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object deleteCmmCode(Object object) throws Exception {
    	return cmmCodeManageDAO.deleteCmmCode(object);
    }

	/**
	 * 소분류코드 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public List<Object> selectCmmDetailCodeList(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCmmDetailCodeList(object);
    }
    
    @Override
    public int selectCmmDetailCodeCount(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCmmDetailCodeCount(object);
    }
    
    @Override
    public List<Object> selectCmmDetailCodeExcelList(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCmmDetailCodeExcelList(object);
    }

	/**
	 * 코드ID 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public List<Object> selectCodeIdList(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCodeIdList(object);
    }

	/**
	 * 소분류코드 리스트 조회 (USE_AT='Y', codeId 기준)
	 * @param object egovMap 등 codeId 포함
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Object> selectLetDetailCodeList(Object object) throws Exception {
		return cmmCodeManageDAO.selectLetDetailCodeList(object);
	}

	@Override
	public List<DetailCodeResponse> getDetailCodeListByCodeId(String codeId) {
		if (codeId == null || codeId.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
		DetailCodeListRequest request = new DetailCodeListRequest(codeId);
		List<DetailCodeResponse> list = cmmCodeManageDAO.selectLetDetailCodeListDto(request);
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		return list;
	}

	@Override
	public List<DetailCodeListByCategoryResponse> getDetailCodeListByCategory(String codeId, String studentCode) {
		if (codeId == null || codeId.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
		DetailCodeListByCategoryRequest request = new DetailCodeListByCategoryRequest(codeId, studentCode);
		List<DetailCodeListByCategoryResponse> list = cmmCodeManageDAO.selectLetDetailCodeListByCategory(request);
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		return list;
	}

	/**
	 * 소분류코드 중복 체크
	 * @param object
	 * @return Y : 가능 N : 불가
	 * @throws Exception
	 */
	@Override
    public String checkCmmDetailCodeDuplication(Object object) throws Exception {
    	return cmmCodeManageDAO.checkCmmDetailCodeDuplication(object);
    }

	/**
	 * 소분류코드 등록
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object insertCmmDetailCode(Object object) throws Exception {
    	return cmmCodeManageDAO.insertCmmDetailCode(object);
    }

	/**
	 * 소분류코드 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object selectCmmDetailCodeDetail(Object object) throws Exception {
    	return cmmCodeManageDAO.selectCmmDetailCodeDetail(object);
    }

	/**
	 * 소분류코드 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object updateCmmDetailCode(Object object) throws Exception {
    	return cmmCodeManageDAO.updateCmmDetailCode(object);
    }

	/**
	 * 소분류코드 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object deleteCmmDetailCode(Object object) throws Exception {
    	return cmmCodeManageDAO.deleteCmmDetailCode(object);
    }
}
