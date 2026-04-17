package arami.common.adminWeb.site.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Repository;

@Repository("siteManageDAO")
public class SiteManageDAO extends EgovAbstractMapper {

	public int insertSite(Object object) throws Exception{
		return insert("siteManageDAO.insertSite", object);
	}

	public int updateSite(Object object) throws Exception{
	    return update("siteManageDAO.updateSite", object);
	}

	public int deleteSite(Object object) throws Exception{
	    return update("siteManageDAO.deleteSite", object);
	}

	public List<EgovMap> selectSiteList(Object object) throws Exception{
        return selectList("siteManageDAO.selectSiteList", object);
    }

	public int selectSiteListCount(Object object) throws Exception{
        return selectOne("siteManageDAO.selectSiteListCount", object);
    }

	public EgovMap selectSiteDetail(Object object) throws Exception{
	    return selectOne("siteManageDAO.selectSiteDetail", object);
	}

	public List<EgovMap> selectSiteSelectList() throws Exception{
	    return selectList("siteManageDAO.selectSiteSelectList");
	}

}
