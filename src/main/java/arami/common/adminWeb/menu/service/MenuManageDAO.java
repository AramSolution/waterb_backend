package arami.common.adminWeb.menu.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Repository;

@Repository("MenuManageDAO")
public class MenuManageDAO extends EgovAbstractMapper {

	/**
	 * 메뉴 목록 조회
	 * @return
	 * @throws Exception
	 */
	public List<EgovMap> selectMenuListT_D() throws Exception {
		return selectList("MenuManageDAO.selectMenuListT_D");
	}

}
