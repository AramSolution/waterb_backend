package arami.userWeb.article.service.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자웹 게시글 상세 조회 응답 (공지/지원사업 등 ARTBBSM 1건)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDetailResponse {

    private Integer nttId;
    private String bbsId;
    private String bbsNm;
    private Long parntscttId;
    private String answerAt;
    private String nttSj;
    private String nttCn;
    /**
     * ARTBBSM.NTT_IMG_FILE_ID — 이미지 파일 그룹 ID(단일 값). 상세 이미지 배열은 {@link #nttImgFiles} 참고.
     */
    private String nttImgFileId;
    private String nttData1;
    private String nttData2;
    private String nttData3;
    private String nttData4;
    private String nttData5;
    private String nttData6;
    private String nttData7;
    private String nttData8;
    private String nttData9;
    private String nttData10;
    private Integer rdcnt;
    private String ntcrId;
    private String ntcrNm;
    private String maskNtcrNm;
    private String ntcrDt;
    private String noticeAt;
    private String secretAt;
    private String atchFileId;
    private String ntcrStartDt;
    private String ntcrEndDt;
    private Integer answerCnt;

    /** 묻고답하기 본문일 때: 답글(관리자 답변) 내용 */
    private String replyContent;
    /** 묻고답하기 본문일 때: 답글 등록일 (표시용) */
    private String replyDate;
    /** 묻고답하기 본문일 때: 답글 작성자명 */
    private String replyNtcrNm;

    /** 목록 순서 기준 이전글(위쪽, 더 최신) */
    private ArticleNavItemResponse prevArticle;
    /** 목록 순서 기준 다음글(아래쪽, 더 오래됨) */
    private ArticleNavItemResponse nextArticle;

    /** 첨부파일 목록 (atchFileId로 조회한 파일 목록, 다운로드 링크용) */
    @Builder.Default
    private List<ArticleFileItemResponse> attacheFiles = new ArrayList<>();

    /**
     * 아카이브 등 NTT_IMG_FILE_ID 그룹에 속한 이미지 목록(seq 오름차순). 그룹 ID 없거나 파일 없으면 빈 배열.
     */
    @Builder.Default
    private List<ArticleFileItemResponse> nttImgFiles = new ArrayList<>();
}
