package arami.common.adminWeb.menu.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import arami.common.adminWeb.menu.service.MenuTreeManageService;
import arami.common.adminWeb.menu.service.MenuTreeManageDAO;

@Service("menuTreeManageService")
public class MenuTreeManageServiceImpl extends EgovAbstractServiceImpl implements MenuTreeManageService {

	@Resource(name = "MenuTreeManageDAO")
	private MenuTreeManageDAO menuTreeManageDAO;

	/**
	 * 메뉴 ID 중복 체크
	 * @param object
	 * @return Y: 등록 가능, N: 등록 불가
	 * @throws Exception
	 */
	@Override
	public String checkMenuIdAjax(Object object) throws Exception {
		return menuTreeManageDAO.checkMenuIdAjax(object);
	}

	/**
	 * 메뉴 등록
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
	public int insertMenuAjax(Object object) throws Exception {
		return menuTreeManageDAO.insertMenuAjax(object);
	}

	/**
	 * 메뉴 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Object> selectMenuList(Object object) throws Exception {
		return menuTreeManageDAO.selectMenuList(object);
	}

	/**
	 * 메뉴 목록 카운트 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
	public int selectMenuListCount(Object object) throws Exception {
		return menuTreeManageDAO.selectMenuListCount(object);
	}

	/**
	 * 메뉴 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object selectMenuDetail(Object object) throws Exception {
		return menuTreeManageDAO.selectMenuDetail(object);
	}

	/**
	 * 메뉴 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
	public int updateMenuAjax(Object object) throws Exception {
		return menuTreeManageDAO.updateMenuAjax(object);
	}

	/**
	 * 메뉴 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
	public int deleteMenuAjax(Object object) throws Exception {
		return menuTreeManageDAO.deleteMenuAjax(object);
	}

}
