package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * ARTAPMM INSERT/UPDATE 한 건 파라미터 (DAO/MyBatis용).
 */
@Data
public class ArtapmmMentorSaveParam {

    private String reqId;
    private String proId;
    private Integer proSeq;
    private String reqEsntlId;
    private String reqPlay;
    private String reqDesc;
    private String fileId;
    private String resultGb;
    private String reqDt;
    private String aprrDt;
    private String chgDt;
    private String stopDt;
    private String reaDesc;
    private String collegeNm;
    private String leaveYn;
    private String majorNm;
    private Integer schoolLvl;
    private String studentId;
    private String hschoolNm;
    private String reqReason;
    private String career;
    /** 희망과목 코드 (CHAR 4) */
    private String reqSub;
    /** 멘토링 희망시간대 (VARCHAR 64, 예: 파이프 구분) */
    private String joinTime;
    private String agree1Yn;
    private String agree2Yn;
    private String sttusCode;
    private String chgUserId;
}
