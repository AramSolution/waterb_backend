package arami.adminWeb.artapps.service.dto.response;

import java.util.Date;

import lombok.Data;

/**
 * ARTAPPM + ARTAPPS (REQ_ID 조인) 신청목록 1행.
 * {@code selectArtappsApplicationList}는 {@code resultType}으로 매핑하며, SQL 컬럼 별칭은 UPPER_SNAKE_CASE(§6.3).
 */
@Data
public class ArtappsApplicationListRowDTO {

    private String rnum;
    private String reqId;
    private String proId;
    private String proSeq;
    private String reqProSeq;
    private String sttusCode;
    private String sttusCodeNm;

    /** 학생명 */
    private String userNm;
    /** 학생 연락처(마스킹) */ 
    private String cmbtlnum;
    /** ARMUSER 학생 회선 전화(USR_TELNO, 복호화·마스킹) — C_ESNTL_ID = U.ESNTL_ID 조인 */
    private String usrTelno;
    private String schoolNm;
    /** 학년반 요약 */
    private String schoolClass;

    private String pUserNm;
    private String mbtlnum;

    /** ARTAPPS */
    private String proGbn;
    private String proGbnEtc;
    private String reqSub;
    private String joinCnt;
    private String befJoin;
    private String joinTime;
    private String joinTimeCon;
    private String sUnder;
    private String sTarget;
    private String sChar;
    private String sReason;
    private String sExpect;
    private String sAppr;
    private String sComm;

    private Date reqDt;
}
