package arami.common.adminWeb.menu.service;

import java.util.List;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;

public interface MenuMakeManageService {

	/**
	 * 메뉴 생성 목록 조회
	 * @param object
	 * @return 메뉴 생성 목록
	 * @throws Exception
	 */
	public List<Object> selectMenuMakeList(Object object) throws Exception;


	/**
	 * 메뉴 생성 내역 조회
	 * @param object
	 * @return 메뉴 생성 목록
	 * @throws Exception
	 */
	List<EgovMap> selectMenuCreatList(Object object) throws Exception;

	/**
	 * 메뉴 생성 저장
	 * @param authorCode 권한코드
	 * @param menuNoList 메뉴번호 리스트
	 * @throws Exception
	 */
	void insertMenuCreatList(String authorCode, List<String> menuNoList) throws Exception;

}