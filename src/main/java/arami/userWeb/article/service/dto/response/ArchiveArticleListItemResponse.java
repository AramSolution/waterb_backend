package arami.userWeb.article.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자웹 아카이브 게시글 목록 1건 응답 DTO.
 * nttImgFileId: NTT_IMG_FILE_ID 그룹 내 첫 이미지(최소 seq)를 "fileId:seq" 형태로 내려줌. 없으면 null.
 * nttImgSaveNm: 해당 썸네일 행의 저장 파일명 — 브라우저 캐시 무효화용.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveArticleListItemResponse {

    private Integer nttId;
    private String bbsId;
    private String nttSj;
    private String nttCn;
    private String nttImgFileId;
    /** 목록 썸네일(최소 seq)의 saveNm. 없으면 null */
    private String nttImgSaveNm;
}
