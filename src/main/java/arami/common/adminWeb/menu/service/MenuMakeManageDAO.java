package arami.common.adminWeb.menu.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;

@Repository("MenuMakeManageDAO")
public class MenuMakeManageDAO extends EgovAbstractMapper {

	/**
	 * 메뉴 생성 목록 조회
	 * @param object
	 * @return 메뉴 생성 목록
	 * @throws Exception
	 */
	public List<Object> selectMenuMakeList(Object object) throws Exception {
		return selectList("MenuMakeManageDAO.selectMenuMakeList", object);
	}

	/**
	 * 메뉴 생성 내역 조회
	 * @param object
	 * @return 메뉴 생성 내역
	 * @throws Exception
	 */
	public List<EgovMap> selectMenuCreatList(Object object) throws Exception {
		return selectList("MenuMakeManageDAO.selectMenuCreatList", object);
	}

	/**
	 * 메뉴 생성 개수 조회
	 * @param object
	 * @return 메뉴 생성 개수
	 * @throws Exception
	 */
	public int selectMenuCreatCnt(Object object) throws Exception {
		return (Integer) selectOne("MenuMakeManageDAO.selectMenuCreatCnt", object);
	}

	/**
	 * 메뉴 생성 삭제
	 * @param object
	 * @throws Exception
	 */
	public void deleteMenuCreat(Object object) throws Exception {
		delete("MenuMakeManageDAO.deleteMenuCreat", object);
	}

	/**
	 * 메뉴 생성 저장
	 * @param object
	 * @throws Exception
	 */
	public void insertMenuCreat(Object object) throws Exception {
		insert("MenuMakeManageDAO.insertMenuCreat", object);
	}

}
