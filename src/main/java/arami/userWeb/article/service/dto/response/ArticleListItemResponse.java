package arami.userWeb.article.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자웹 게시글 목록 1건 응답 DTO (공지/지원사업 등 ARTBBSM)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleListItemResponse {

    private Integer nttId;
    private String bbsId;
    private String nttSj;
    private String ntcrNm;
    private String ntcrDt;
    private Integer rdcnt;
    private String noticeAt;
    private String atchFileId;
    /** 묻고답하기용: 답글 개수(0이면 답변대기, 1 이상이면 답변완료) */
    private Integer answerCnt;
    /** 비밀글 여부 (Y/N) - Y이면 상세 진입 시 /userWeb/qna/pw로 이동 */
    private String secretAt;
}
