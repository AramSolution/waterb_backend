package arami.userWeb.article.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자웹 게시글 상세 응답 내 이전글/다음글 한 건 (nttId, 제목만)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleNavItemResponse {

    private Integer nttId;
    private String nttSj;
}
