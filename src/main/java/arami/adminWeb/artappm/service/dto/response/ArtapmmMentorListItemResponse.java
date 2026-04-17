package arami.adminWeb.artappm.service.dto.response;

import lombok.Data;

/**
 * 지원사업 멘토 목록 1건 (ARTAPMM + ARMUSER).
 */
@Data
public class ArtapmmMentorListItemResponse {
    private String reqId;
    private String proId;
    private Integer proSeq;
    private String reqEsntlId;
    private String resultGb;
    private String resultGbNm;
    private String sttusCode;
    private String userNm;
    private String mbtlnum;
    private String profileDesc;
}
