package arami.common.adminWeb.menu.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

@Repository("MenuTreeManageDAO")
public class MenuTreeManageDAO extends EgovAbstractMapper {

	/**
	 * 메뉴 ID 중복 체크
	 * @param object
	 * @return Y: 등록 가능, N: 등록 불가
	 * @throws Exception
	 */
	public String checkMenuIdAjax(Object object) throws Exception {
		return selectOne("MenuTreeManageDAO.checkMenuIdAjax", object);
	}

	/**
	 * 메뉴 등록
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int insertMenuAjax(Object object) throws Exception {
		return insert("MenuTreeManageDAO.insertMenuAjax", object);
	}

	/**
	 * 메뉴 목록 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<Object> selectMenuList(Object object) throws Exception {
		return selectList("MenuTreeManageDAO.selectMenuList", object);
	}

	/**
	 * 메뉴 목록 카운트 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int selectMenuListCount(Object object) throws Exception {
		return selectOne("MenuTreeManageDAO.selectMenuListCount", object);
	}

	/**
	 * 메뉴 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object selectMenuDetail(Object object) throws Exception {
		return selectOne("MenuTreeManageDAO.selectMenuDetail", object);
	}

	/**
	 * 메뉴 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int updateMenuAjax(Object object) throws Exception {
		return update("MenuTreeManageDAO.updateMenuAjax", object);
	}

	/**
	 * 메뉴 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int deleteMenuAjax(Object object) throws Exception {
		return delete("MenuTreeManageDAO.deleteMenuAjax", object);
	}

}
