package arami.adminWeb.banner.service.dto.request;

import lombok.Data;

@Data
public class BannerListRequest {
    private Integer startIndex;
    private Integer lengthPage;
    private String searchCondition;
    private String searchKeyword;
    private String banrGb;
    private String userGbn;

    public void setDefaultPaging() {
        if (startIndex == null || startIndex < 0) {
            startIndex = 0;
        }
        if (lengthPage == null || lengthPage <= 0) {
            lengthPage = 15;
        }
    }
}
