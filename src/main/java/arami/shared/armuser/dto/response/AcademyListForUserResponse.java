package arami.shared.armuser.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AcademyListForUserResponse {

    private List<AcademyListForUserItem> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
