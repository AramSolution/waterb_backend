package arami.adminWeb.artprom.service.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 지원사업 목록 조회 응답 DTO
 */
@Data
public class ArtpromListResponse {

    private List<ArtpromDTO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
