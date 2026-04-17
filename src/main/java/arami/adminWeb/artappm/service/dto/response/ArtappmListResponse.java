package arami.adminWeb.artappm.service.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ArtappmListResponse {
    private List<ArtappmDTO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
