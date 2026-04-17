package arami.common.adminWeb.article.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Repository;

@Repository("articleManageDAO")
public class ArticleManageDAO extends EgovAbstractMapper {

    public List<EgovMap> selectArticleList(Object object) throws Exception{
        return selectList("articleManageDAO.selectArticleList", object);
    }

    public int selectArticleListCount(Object object) throws Exception{
        return selectOne("articleManageDAO.selectArticleListCount", object);
    }

    /** 사용자웹 묻고답하기용: 본문만 조회(ANSWER_AT='N') */
    public List<EgovMap> selectArticleListParentOnly(Object object) throws Exception {
        return selectList("articleManageDAO.selectArticleListParentOnly", object);
    }

    public int selectArticleListParentOnlyCount(Object object) throws Exception {
        return selectOne("articleManageDAO.selectArticleListParentOnlyCount", object);
    }

    public EgovMap selectArticleDetail(Object object) throws Exception{
        return selectOne("articleManageDAO.selectArticleDetail", object);
    }

    /** 사용자웹 상세 전용: ANSWER_CNT는 답글만 카운트 */
    public EgovMap selectArticleDetailForUser(Object object) throws Exception {
        return selectOne("articleManageDAO.selectArticleDetailForUser", object);
    }

    /** 묻고답하기 본문의 답글 1건 (관리자 답변 표시용) */
    public EgovMap selectReplyByParntscttId(Object object) throws Exception {
        return selectOne("articleManageDAO.selectReplyByParntscttId", object);
    }

    public EgovMap selectPrevArticle(Object object) throws Exception {
        return selectOne("articleManageDAO.selectPrevArticle", object);
    }

    public EgovMap selectPrevArticleWhenNonNotice(Object object) throws Exception {
        return selectOne("articleManageDAO.selectPrevArticleWhenNonNotice", object);
    }

    public EgovMap selectNextArticle(Object object) throws Exception {
        return selectOne("articleManageDAO.selectNextArticle", object);
    }

    public int updateViewCount(Object object) throws Exception{
        return update("articleManageDAO.updateViewCount", object);
    }

	public int insertArticle(Object object) throws Exception{
        return insert("articleManageDAO.insertArticle", object);
    }
	
	public int insertArticle2(Object object) throws Exception{
        return insert("articleManageDAO.insertArticle2", object);
    }
	
	public int replyArticle(Object object) throws Exception {
		return insert("articleManageDAO.replyArticle", object);
	}

	public int updateArticle(Object object) throws Exception{
	    return update("articleManageDAO.updateArticle", object);
	}

	public int deleteArticle(Object object) throws Exception{
	    return update("articleManageDAO.deleteArticle", object);
	}

	public int confirmPasswd(Object object) throws Exception{
	    return selectOne("articleManageDAO.confirmPasswd", object);
	}

	public int getNextNttId(Object object) throws Exception{
	    return selectOne("articleManageDAO.getNextNttId", object);
	}

	public int deleteArticleMultiFile(Object object) throws Exception{
	    return update("articleManageDAO.deleteArticleMultiFile", object);
	}

	public List<EgovMap> selectPreTotalBbsList(Object object) throws Exception{
	    return selectList("articleManageDAO.selectPreTotalBbsList", object);
	}

	public List<EgovMap> selectPreViewBbsList(Object object) throws Exception{
	    return selectList("articleManageDAO.selectPreViewBbsList", object);
	}

}
