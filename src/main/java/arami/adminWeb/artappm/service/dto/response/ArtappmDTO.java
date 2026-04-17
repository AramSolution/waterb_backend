package arami.adminWeb.artappm.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;

/**
 * 지원사업 신청 정보 DTO (목록/상세 공통)
 */
@Data
public class ArtappmDTO {

    /** 지원사업신청ID (ARTAPPM PK) */
    private String reqId;
    private String proId;
    /** 지원사업 구분 (ARTPROM.PRO_GB, 상세 조회 시 서브쿼리) */
    private String proGb;
    private String proSeq;
    /** 신청회차 (PRO_SEQ와 동일 값) */
    private String reqProSeq;
    private String reqEsntlId;
    /** 학생(자녀) 고유ID (ARTAPPM.C_ESNTL_ID) */
    @JsonProperty("cEsntlId")
    private String cEsntlId;
    /** 신청자명 (ARMUSER, 학생 조인 기준) */
    private String userNm;
    /** 신청자 연락처(마스킹, ARMUSER) */
    @JsonProperty("cMbtlnum")
    private String cMbtlnum;
    /** 신청자 성별코드 (ARMUSER) */
    @JsonProperty("cSexdstnCode")
    private String cSexdstnCode;
    /** 신청자 생년월일 (ARMUSER) */
    @JsonProperty("cBrthdy")
    private String cBrthdy;
    /** 세대주명 */
    private String headNm;
    /** 보호자 고유ID */
    @JsonProperty("pEsntlId")
    private String pEsntlId;
    /** 보호자명 */
    @JsonProperty("pUserNm")
    private String pUserNm;
    /** 프로그램 유형 (01:1인탐구형, 02:모둠탐구형) */
    private String proType;
    /** 프로그램 유형명 */
    private String proTypeNm;
    private String mbtlnum;
    private String brthdy;
    /** 학부모 주민번호 */
    @JsonProperty("pIhidnum")
    private String pIhidnum;
    /** 학생 주민번호 */
    @JsonProperty("cIhidnum")
    private String cIhidnum;
    private String certYn;
    private String schoolId;
    private String schoolGb;
    private String schoolNm;
    private Integer schoolLvl;
    private Integer schoolNo;
    /** 학년반 표시 (예: 1학년 2반) */
    private String schoolClass;
    private String payBankCode;
    /** 은행명 */
    private String payBankCodeNm;
    private String payBank;
    /** 예금주 */
    private String holderNm;
    private String reqPart;
    private String playPart;
    private String reqObj;
    private String reqPlay;
    private String reqPlan;
    /** 다자녀유무 */
    private String mchilYn;
    /** 다자녀명 */
    private String mchilNm;
    private String reqDesc;
    private String fileId;
    /** 수강확인증 파일 ID (ARTFILE) */
    private String studyCert;
    private String resultGb;
    /** 신청일시 */
    private Date reqDt;
    /** 승인일시 */
    private Date aprrDt;
    /** 변경일시 */
    private Date chgDt;
    /** 중단일시 */
    private Date stopDt;
    /** 사유 */
    private String reaDesc;
    /** 상담일자 (ARTAPPM.WORK_DT, YYYY-MM-DD) */
    private String workDt;
    private String sttusCode;
    /** 상태명 */
    private String sttusCodeNm;
    private String chgUserId;
    private Date crtDate;
    private Date chgDate;
    /** 프로그램 일자 (ARTAPPM.WORK_DATE, YYYY-MM-DD) */
    private String workDate;
    /** 프로그램 시작시간 (ARTAPPM.START_TIME, HH:MM) */
    private String startTime;
    /** 프로그램 항목1 (ARTAPPM.ITEM1) */
    private String item1;
    /** 프로그램 항목2 (ARTAPPM.ITEM2) */
    private String item2;
    /** 프로그램 항목3 (ARTAPPM.ITEM3) */
    private String item3;
    /** 프로그램 항목4 (ARTAPPM.ITEM4) */
    private String item4;
    /** 우편번호 (ARMUSER) */
    private String zip;
    /** 주소 (ARMUSER) */
    private String adres;
    /** 상세주소 (ARMUSER) */
    private String detailAdres;
    /** 전체주소 (주소 + 상세주소) */
    private String fullAdres;
    /** 부모관계 (보호자 PU.RELATION_GB: F=부, M=모, E=기타) */
    private String relationGb;
    /** 부모관계명 */
    private String relationGbNm;
    /** 농어촌학교여부 (신청자 U.FARM_YN) */
    private String farmYn;
    /** 학교소재지 (신청자 U.FARM_DESC) */
    private String farmDesc;
    private String rnum;

    /** ARTAPPS (공부의 명수) — 상세 조회 시 JOIN */
    @JsonProperty("appsProGbn")
    private String appsProGbn;
    @JsonProperty("appsProGbnEtc")
    private String appsProGbnEtc;
    @JsonProperty("appsReqSub")
    private String appsReqSub;
    @JsonProperty("appsJoinCnt")
    private String appsJoinCnt;
    @JsonProperty("appsBefJoin")
    private String appsBefJoin;
    @JsonProperty("appsJoinTime")
    private String appsJoinTime;
    @JsonProperty("appsJoinTimeCon")
    private String appsJoinTimeCon;
    @JsonProperty("appsSUnder")
    private String appsSUnder;
    @JsonProperty("appsSTarget")
    private String appsSTarget;
    @JsonProperty("appsSChar")
    private String appsSChar;
    @JsonProperty("appsSReason")
    private String appsSReason;
    @JsonProperty("appsSExpect")
    private String appsSExpect;
    @JsonProperty("appsSAppr")
    private String appsSAppr;
    @JsonProperty("appsSComm")
    private String appsSComm;
    @JsonProperty("appsAgree1Yn")
    private String appsAgree1Yn;
    @JsonProperty("appsAgree2Yn")
    private String appsAgree2Yn;
    @JsonProperty("appsAgree3Yn")
    private String appsAgree3Yn;
}
