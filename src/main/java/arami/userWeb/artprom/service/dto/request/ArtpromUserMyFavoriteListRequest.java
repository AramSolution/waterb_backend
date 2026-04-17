package arami.userWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * 내가 즐겨찾기한 지원사업 목록 조회 요청 (MY PAGE 즐겨찾기).
 * reqEsntlId는 서비스에서 로그인 사용자로 설정.
 */
@Data
public class ArtpromUserMyFavoriteListRequest {

    /** 로그인 사용자 고유ID(서비스에서 설정) */
    private String reqEsntlId;

    /** Datatables 호환 시작 인덱스 */
    private Integer start;
    /** Datatables 호환 조회 건수 */
    private Integer length;

    /** MyBatis LIMIT OFFSET용 시작 인덱스 */
    private Integer startIndex;
    /** MyBatis LIMIT OFFSET용 조회 건수 */
    private Integer lengthPage;

    public void setDefaultPaging() {
        if (this.start == null) {
            this.start = 0;
        }
        if (this.length == null) {
            this.length = 15;
        }
        this.lengthPage = this.length;
        this.startIndex = this.start;
    }
}
