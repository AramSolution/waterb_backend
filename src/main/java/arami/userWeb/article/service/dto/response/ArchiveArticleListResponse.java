package arami.userWeb.article.service.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자웹 아카이브 게시글 목록 조회 응답
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveArticleListResponse {

    private List<ArchiveArticleListItemResponse> data;
    /** 검색어와 무관한 전체 게시물 수 (bbsId 기준) */
    private int totalRegisteredCount;
    private int recordsTotal;
    private int recordsFiltered;
}
