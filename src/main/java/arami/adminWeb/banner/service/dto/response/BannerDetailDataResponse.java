package arami.adminWeb.banner.service.dto.response;

import lombok.Data;

@Data
public class BannerDetailDataResponse {
    private String banrCd;
    private String banrGb;
    private String title;
    private String body;
    private String linkGb;
    private String linkUrl;
    private String imgUrl;
    private String orgfNm;
    private String fileCd;
    private Integer widthSize;
    private Integer heightSize;
    private Integer posiX;
    private Integer posiY;
    private String startDt;
    private String endDt;
    private String startDttm;
    private String endDttm;
    private Integer orderBy;
    private String statCode;
}
