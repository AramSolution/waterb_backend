package arami.userWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * 사용자웹 지원사업 즐겨찾기 저장 요청 DTO.
 */
@Data
public class ArtpromUserFavoriteRequest {

    /** 지원사업 코드(PRO_ID). */
    private String proId;

    /** 사용자 고유ID(ESNTL_ID). 서버에서 로그인 사용자로 세팅. */
    private String esntlId;
}
