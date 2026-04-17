package arami.common.adminWeb.site.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import arami.common.adminWeb.site.service.SiteManageService;
import arami.common.adminWeb.site.service.SiteManageDAO;

@Service("siteManageService")
public class SiteManageServiceImpl extends EgovAbstractServiceImpl implements SiteManageService {

	@Resource(name = "siteManageDAO")
	private SiteManageDAO siteManageDAO;

	@Resource(name="egovSiteManageIdGnrService")
    private EgovIdGnrService idgenService;

    @Override
    public int insertSite(Object object) throws Exception {
        ModelMap model = (ModelMap)object;

        model.put("siteId", idgenService.getNextStringId());

        return siteManageDAO.insertSite(model);
    }

    @Override
    public int updateSite(Object object) throws Exception {
        return siteManageDAO.updateSite(object);
    }

    @Override
    public int deleteSite(Object object) throws Exception {
        return siteManageDAO.deleteSite(object);
    }

    @Override
    public List<EgovMap> selectSiteList(Object object) throws Exception {
        return siteManageDAO.selectSiteList(object);
    }

    @Override
    public int selectSiteListCount(Object object) throws Exception {
        return siteManageDAO.selectSiteListCount(object);
    }

    @Override
    public EgovMap selectSiteDetail(Object object) throws Exception {
        return siteManageDAO.selectSiteDetail(object);
    }

    @Override
    public List<EgovMap> selectSiteSelectList() throws Exception {
        return siteManageDAO.selectSiteSelectList();
    }

}
