package arami.common.adminWeb.menu.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;

public interface MenuManageService {

	/**
	 * 메뉴 목록 조회
	 * @return
	 * @throws Exception
	 */
	public List<EgovMap> selectMenuListT_D() throws Exception;

}
