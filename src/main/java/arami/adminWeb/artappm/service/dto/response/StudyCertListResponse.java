package arami.adminWeb.artappm.service.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class StudyCertListResponse {
    private List<StudyCertListItemResponse> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;

    public void setData(List<StudyCertListItemResponse> data) { this.data = data; }
    public void setRecordsTotal(Integer recordsTotal) { this.recordsTotal = recordsTotal; }
    public void setRecordsFiltered(Integer recordsFiltered) { this.recordsFiltered = recordsFiltered; }
    public void setResult(String result) { this.result = result; }
}
