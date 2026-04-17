package arami.common.adminWeb.menu.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import arami.common.adminWeb.menu.service.MenuMakeManageDAO;
import arami.common.adminWeb.menu.service.MenuMakeManageService;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;

@Service("menuMakeManageService")
public class MenuMakeManageServiceImpl extends EgovAbstractServiceImpl implements MenuMakeManageService {

	@Resource(name = "MenuMakeManageDAO")
	private MenuMakeManageDAO menuMakeManageDAO;

	/**
	 * 메뉴 생성 목록 조회
	 * @param object
	 * @return 메뉴 생성 목록
	 * @throws Exception
	 */
	@Override
	public List<Object> selectMenuMakeList(Object object) throws Exception{
		return menuMakeManageDAO.selectMenuMakeList(object);
	}

	/**
	 * 메뉴 생성 내역 조회
	 * @param object
	 * @return 메뉴 생성 내역
	 * @throws Exception
	 */
	@Override
	public List<EgovMap> selectMenuCreatList(Object object) throws Exception {
		return menuMakeManageDAO.selectMenuCreatList(object);
	}

	/**
	 * 메뉴 생성 저장
	 * @param authorCode 권한코드
	 * @param menuNoList 메뉴번호 리스트
	 * @throws Exception
	 */
	@Override
	public void insertMenuCreatList(String authorCode, List<String> menuNoList) throws Exception {
		// 기존 데이터 개수 확인
		java.util.HashMap<String, Object> paramMap = new java.util.HashMap<>();
		paramMap.put("authorCode", authorCode);
		int count = menuMakeManageDAO.selectMenuCreatCnt(paramMap);
		
		// 기존 데이터가 있으면 삭제
		if (count > 0) {
			menuMakeManageDAO.deleteMenuCreat(paramMap);
		}
		
		// 새로운 메뉴 생성 데이터 저장
		for (String menuNo : menuNoList) {
			java.util.HashMap<String, Object> insertMap = new java.util.HashMap<>();
			insertMap.put("authorCode", authorCode);
			insertMap.put("menuNo", menuNo);
			menuMakeManageDAO.insertMenuCreat(insertMap);
		}
	}

}
