package arami.adminWeb.artappm.service.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class ArtapmmApplicationListResponse {
    private List<ArtapmmApplicationListItemResponse> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
