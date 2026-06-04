package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class SupportDrainageEquipUnpaidListResponse {

    private String result;
    private String message;
    private Integer recordsFiltered;
    private Integer recordsTotal;
    private List<SupportDrainageEquipListItemResponse> data;
}
