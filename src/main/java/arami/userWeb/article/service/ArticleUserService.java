package arami.userWeb.article.service;

import arami.userWeb.article.service.dto.response.ArticleDetailResponse;
import arami.userWeb.article.service.dto.response.ArticleListResponse;
import arami.userWeb.article.service.dto.response.ArchiveArticleDetailNavResponse;
import arami.userWeb.article.service.dto.response.ArchiveArticleListResponse;

/**
 * 사용자웹 게시판(공지/지원사업 등) 목록·상세 조회 서비스
 */
public interface ArticleUserService {

    /**
     * 게시글 목록 조회 (공지 우선, 노출기간·사용중만)
     *
     * @param bbsId           게시판 ID (필수)
     * @param limit           조회 건수 (기본 20)
     * @param offset          시작 위치 (기본 0)
     * @param searchCondition 검색 조건 (1: 제목, 2: 내용, null 가능)
     * @param searchKeyword   검색어 (null 가능)
     * @return 목록 + 전체 건수
     */
    ArticleListResponse getArticleList(String bbsId, Integer limit, Integer offset,
                                       String searchCondition, String searchKeyword);

    /**
     * 아카이브 게시글 목록 조회 (사용자웹 전용)
     * - 조회 컬럼: NTT_ID, BBS_ID, NTT_SJ, NTT_CN, NTT_IMG_FILE_ID
     * - 검색: searchKeyword + ArticleManage searchCondition (1~4). 키워드만 있고 조건 생략 시 1(통합)
     * - 페이징: limit/offset
     *
     * @param bbsId             게시판 ID (필수)
     * @param limit             조회 건수 (기본 20)
     * @param offset            시작 위치 (기본 0)
     * @param searchKeyword     검색어 (null 가능)
     * @param searchCondition   1 통합, 2 제목, 3 작성자, 4 내용 (null·빈 문자면 키워드 있을 때 1)
     * @return 목록 + 전체 건수
     */
    ArchiveArticleListResponse getArchiveArticleList(
            String bbsId, Integer limit, Integer offset, String searchKeyword, String searchCondition);

    /**
     * 게시글 상세 조회 (조회수 +1 처리 후 반환)
     *
     * @param bbsId 게시판 ID (필수)
     * @param nttId 게시글 ID (필수)
     * @return 상세 DTO, 없으면 null
     */
    ArticleDetailResponse getArticleDetail(String bbsId, Integer nttId);

    /**
     * 아카이브 상세 조회 (사용자웹 전용)
     *
     * @param bbsId 게시판 ID (필수)
     * @param nttId 게시글 ID (필수)
     * @return 상세 DTO, 없으면 null
     */
    ArchiveArticleDetailNavResponse getArchiveArticleDetail(String bbsId, Integer nttId);

    /**
     * 비밀글 비밀번호 확인 (일치 시 true)
     *
     * @param bbsId    게시판 ID (필수)
     * @param nttId    게시글 ID (필수)
     * @param password 비밀번호 (평문, 서비스에서 암호화 후 DB와 비교)
     * @return 일치 시 true, 불일치/예외 시 false
     */
    boolean confirmArticlePassword(String bbsId, Integer nttId, String password);

    /**
     * 게시판 비밀글 사용 여부(ARMBORD.SECRET_YN) 조회.
     * 글쓰기 화면에서 비밀번호 입력란 노출 여부 판단용.
     *
     * @param bbsId 게시판 ID (필수)
     * @return "Y" 또는 "N", 조회 실패 시 "N"
     */
    String getBoardSecretYn(String bbsId);
}
