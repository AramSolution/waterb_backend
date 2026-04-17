package arami.adminWeb.banner.service.dto.request;

import lombok.Data;

@Data
public class BannerSaveRequest {
    private String banrCd;
    private String banrGb;
    private String title;
    private String body;
    private String linkGb;
    private String linkUrl;
    private String imgUrl;
    private String fileCd;
    private Integer widthSize;
    private Integer heightSize;
    private Integer posiX;
    private Integer posiY;
    private String startDt;
    private String endDt;
    private Integer orderBy;
    private String statCode;
    private String UNIQ_ID;
    private Boolean removeImage;

    public String getStartDttm() {
        if (startDt == null || startDt.trim().isEmpty()) {
            return null;
        }
        return startDt.trim() + ":00";
    }

    public String getEndDttm() {
        if (endDt == null || endDt.trim().isEmpty()) {
            return null;
        }
        return endDt.trim() + ":59";
    }
}
