package arami.adminWeb.banner.service.dto.response;

import lombok.Data;

@Data
public class BannerItemResponse {
    private String rnum;
    private String banrCd;
    private String banrGb;
    private String title;
    private String linkGb;
    private String linkUrl;
    private String imgUrl;
    private String imgUrl1;
    private String fileCd;
    private Integer widthSize;
    private Integer heightSize;
    private Integer posiX;
    private Integer posiY;
    private String startDttm;
    private String endDttm;
    private Integer orderBy;
    private String statCode;
}
