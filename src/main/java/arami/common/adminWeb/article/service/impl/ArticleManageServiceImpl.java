package arami.common.adminWeb.article.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import arami.common.adminWeb.article.service.ArticleManageService;
import arami.common.adminWeb.article.service.ArticleManageDAO;
import egovframework.let.utl.sim.service.EgovFileScrty;

@Service("articleManageService")
public class ArticleManageServiceImpl extends EgovAbstractServiceImpl implements ArticleManageService {

    @Resource(name = "egovNttIdGnrService")
    private EgovIdGnrService nttIdgenService;

	@Resource(name = "articleManageDAO")
	private ArticleManageDAO articleManageDAO;

	@Override
    public List<EgovMap> selectArticleList(Object object) throws Exception {
        return articleManageDAO.selectArticleList(object);
    }

    @Override
    public int selectArticleListCount(Object object) throws Exception {
        return articleManageDAO.selectArticleListCount(object);
    }

    @Override
    public List<EgovMap> selectArticleListParentOnly(Object object) throws Exception {
        return articleManageDAO.selectArticleListParentOnly(object);
    }

    @Override
    public int selectArticleListParentOnlyCount(Object object) throws Exception {
        return articleManageDAO.selectArticleListParentOnlyCount(object);
    }

    @Override
    public EgovMap selectArticleDetail(Object object) throws Exception {
        return articleManageDAO.selectArticleDetail(object);
    }

    @Override
    public EgovMap selectArticleDetailForUser(Object object) throws Exception {
        return articleManageDAO.selectArticleDetailForUser(object);
    }

    @Override
    public EgovMap selectReplyByParntscttId(Object object) throws Exception {
        return articleManageDAO.selectReplyByParntscttId(object);
    }

    @Override
    public EgovMap selectPrevArticle(Object object) throws Exception {
        return articleManageDAO.selectPrevArticle(object);
    }

    @Override
    public EgovMap selectPrevArticleWhenNonNotice(Object object) throws Exception {
        return articleManageDAO.selectPrevArticleWhenNonNotice(object);
    }

    @Override
    public EgovMap selectNextArticle(Object object) throws Exception {
        return articleManageDAO.selectNextArticle(object);
    }

    @Override
    public int updateViewCount(Object object) throws Exception {
        return articleManageDAO.updateViewCount(object);
    }

    @Override
    public int insertArticle(Object object) throws Exception {
        ModelMap model = (ModelMap) object;

        model.addAttribute("nttId", nttIdgenService.getNextIntegerId());

        // parntscttId가 null이거나 빈 문자열일 때 nttId로 설정
        if(model.get("parntscttId") == null || model.get("parntscttId").toString().equals("")) {
            model.put("parntscttId", model.get("nttId").toString());
        }

        return articleManageDAO.insertArticle(object);
    }
    
    @Override
    public int insertArticle2(Object object) throws Exception {
        ModelMap model = (ModelMap) object;

        model.addAttribute("nttId", nttIdgenService.getNextIntegerId());

        // parntscttId가 null이거나 빈 문자열일 때 nttId로 설정
        if(model.get("parntscttId") == null || model.get("parntscttId").toString().equals("")) {
            model.put("parntscttId", model.get("nttId").toString());
        }

        return articleManageDAO.insertArticle2(object);
    }
    
    @Override
	public int replyArticle(Object object) throws Exception {
		return articleManageDAO.replyArticle(object);
	}

    @Override
    public int updateArticle(Object object) throws Exception {
        return articleManageDAO.updateArticle(object);
    }

    @Override
    public int deleteArticle(Object object) throws Exception {
        return articleManageDAO.deleteArticle(object);
    }

    @Override
    public int confirmPasswd(Object object) throws Exception {
        ModelMap model = (ModelMap) object;

        model.put("password", EgovFileScrty.encryptPassword(model.get("password").toString(), ""));

        return articleManageDAO.confirmPasswd(object);
    }

    //게시글 번호 채번
    @Override
    public int getNextNttId(Object object) throws Exception {
        return articleManageDAO.getNextNttId(object);
    }

    @Override
    public int deleteArticleMultiFile(Object object) throws Exception {
        return articleManageDAO.deleteArticleMultiFile(object);
    }

    @Override
    public List<EgovMap> selectPreTotalBbsList(Object object) throws Exception {
        return articleManageDAO.selectPreTotalBbsList(object);
    }

    @Override
    public List<EgovMap> selectPreViewBbsList(Object object) throws Exception {
        return articleManageDAO.selectPreViewBbsList(object);
    }

}
