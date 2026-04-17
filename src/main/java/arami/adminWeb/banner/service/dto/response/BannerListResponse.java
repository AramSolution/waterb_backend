package arami.adminWeb.banner.service.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class BannerListResponse {
    private String result;
    private Integer recordsFiltered;
    private Integer recordsTotal;
    private List<BannerItemResponse> data;
}
