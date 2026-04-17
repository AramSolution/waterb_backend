package arami.userWeb.article.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자웹 아카이브 상세(이전/현재/다음) 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveArticleDetailNavResponse {

    private ArticleDetailResponse prevArchive;
    private ArticleDetailResponse currentArchive;
    private ArticleDetailResponse nextArchive;
}
