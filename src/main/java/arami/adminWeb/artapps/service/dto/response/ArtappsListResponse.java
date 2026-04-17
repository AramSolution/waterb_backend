package arami.adminWeb.artapps.service.dto.response;    

import lombok.Data;

import java.util.List;

/**
 * 공부의명수 목록 조회 응답 DTO
 */
@Data
public class ArtappsListResponse {

    private List<ArtappsDTO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
