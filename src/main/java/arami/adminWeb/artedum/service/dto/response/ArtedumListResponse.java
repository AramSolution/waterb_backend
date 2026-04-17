package arami.adminWeb.artedum.service.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ArtedumListResponse {

    private List<ArtedumDTO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
