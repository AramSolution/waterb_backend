package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class SupportDrainageEquipExcelListResponse {

    private List<SupportDrainageEquipListItemResponse> data;
    private String result;
}
