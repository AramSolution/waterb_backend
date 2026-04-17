package arami.userWeb.artprom.service.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 지원사업 목록 조회 응답 DTO (사용자웹)
 */
@Data
public class ArtpromUserListResponse {

    private List<ArtpromUserListDTO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
