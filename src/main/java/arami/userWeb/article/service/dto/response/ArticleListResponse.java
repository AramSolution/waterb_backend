package arami.userWeb.article.service.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자웹 게시글 목록 조회 응답 (목록 + 전체 건수)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleListResponse {

    private List<ArticleListItemResponse> data;
    private int recordsTotal;
    private int recordsFiltered;
}
