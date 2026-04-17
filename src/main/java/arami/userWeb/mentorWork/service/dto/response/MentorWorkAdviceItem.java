package arami.userWeb.mentorWork.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 멘토업무(mentorWork) 화면 - 정보조회 아래 목록 1건.
 * ARTADVI + ARTAPPM + ARMUSER 조인 결과. (ARTADVI.STTUS_CODE 미사용)
 */
@Data
public class MentorWorkAdviceItem {

    private String reqId;
    private String proId;
    private String proSeq;
    private String proGb;
    private String reqEsntlId;
    private String advEsntlId;
    @JsonProperty("pEsntlId")
    private String pEsntlId;
    @JsonProperty("pUserNm")
    private String pUserNm;
    @JsonProperty("pMbtlnum")
    private String pMbtlnum;
    private String schoolId;
    private String schoolGb;
    private String schoolNm;
    private String schoolLvl;
    private String schoolLvlNm;
    private String schoolNo;
    private String schoolNoNm;
    private String reqEsntlNm;
    private String mbtlnum;
    private String zip;
    private String adres;
    private String detailAdres;
    private String fullAdres;
}
