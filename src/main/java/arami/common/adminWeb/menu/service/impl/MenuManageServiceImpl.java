package arami.common.adminWeb.menu.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Service;

import arami.common.adminWeb.menu.service.MenuManageService;
import arami.common.adminWeb.menu.service.MenuManageDAO;

@Service("menuManageService")
public class MenuManageServiceImpl extends EgovAbstractServiceImpl implements MenuManageService {

	@Resource(name = "MenuManageDAO")
	private MenuManageDAO menuManageDAO;

	/**
	 * 메뉴 목록 조회
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<EgovMap> selectMenuListT_D() throws Exception {
		return menuManageDAO.selectMenuListT_D();
	}

}
