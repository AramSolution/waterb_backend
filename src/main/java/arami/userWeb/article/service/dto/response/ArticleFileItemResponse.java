package arami.userWeb.article.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자웹 게시글 상세 첨부파일 1건 (다운로드 링크용 fileId, seq, 원본파일명)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleFileItemResponse {

    /** 파일 그룹 ID (문자열로 직렬화하여 JS 정밀도 이슈 방지) */
    private String fileId;
    /** 파일 순번 */
    private Integer seq;
    /** 원본 파일명 */
    private String orgfNm;
    /** 저장 파일명(ARTFILE). 미리보기 URL 캐시 무효화용 */
    private String saveNm;
}
