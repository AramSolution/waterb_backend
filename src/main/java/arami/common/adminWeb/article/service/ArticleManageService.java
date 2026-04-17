package arami.common.adminWeb.article.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;

public interface ArticleManageService {

    /**
     * 게시글 목록 조회
     * @param object
     * @return
     * @throws Exception
     */
    public List<EgovMap> selectArticleList(Object object) throws Exception;
    public int selectArticleListCount(Object object) throws Exception;

    /** 사용자웹 묻고답하기용: 본문만 조회(ANSWER_AT='N') */
    List<EgovMap> selectArticleListParentOnly(Object object) throws Exception;
    int selectArticleListParentOnlyCount(Object object) throws Exception;

    /**
     * 게시글 상세 조회
     * @param object
     * @return
     * @throws Exception
     */
    public EgovMap selectArticleDetail(Object object) throws Exception;

    /** 사용자웹 상세 전용: ANSWER_CNT는 답글만 카운트 */
    EgovMap selectArticleDetailForUser(Object object) throws Exception;

    /** 묻고답하기 본문의 답글 1건 (관리자 답변 표시용) */
    EgovMap selectReplyByParntscttId(Object object) throws Exception;

    EgovMap selectPrevArticle(Object object) throws Exception;
    EgovMap selectPrevArticleWhenNonNotice(Object object) throws Exception;
    EgovMap selectNextArticle(Object object) throws Exception;

    /**
     * 조회수 업데이트
     * @param object
     * @return
     * @throws Exception
     */
    public int updateViewCount(Object object) throws Exception;

    /**
     * 게시글등록
     * @param object
     * @return
     * @throws Exception
     */
	public int insertArticle(Object object) throws Exception;
	public int insertArticle2(Object object) throws Exception;

	/**
     * 답글등록
     * @param object
     * @return
     * @throws Exception
     */
	public int replyArticle(Object object) throws Exception;

	/**
	 * 게시글 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int updateArticle(Object object) throws Exception;

	/**
	 * 게시글 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int deleteArticle(Object object) throws Exception;

	/**
	 * 비밀번호 확인
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int confirmPasswd(Object object) throws Exception;

	/**
	 * 게시글 번호 채번
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int getNextNttId(Object object) throws Exception;

	/**
	 * 게시글 사진 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int deleteArticleMultiFile(Object object) throws Exception;

	/**
	 * 게시판 전체 미리보기 조회
	 * @param object
	 * @return
	 */
	public List<EgovMap> selectPreTotalBbsList(Object object) throws Exception;

	/**
	 * 게시판 미리보기
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<EgovMap> selectPreViewBbsList(Object object) throws Exception;

}
