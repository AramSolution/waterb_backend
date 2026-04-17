package arami.shared.armuser.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ArmuserListResponse {

    private List<ArmuserDTO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
