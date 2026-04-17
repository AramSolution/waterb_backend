package arami.adminWeb.artedum.service.dto.response;

import lombok.Data;

/**
 * 가맹학원(희망사업 신청) 목록용 DTO (ARMUSER 컬럼 + 취급과목 SUB_NM_LIST)
 */
@Data
public class ArtedumDTO {

    /** 목록 행 번호 (RNUM) */
    private Integer rnum;
    private String esntlId;
    private String userId;
    private String userNm;
    private String emailAdres;
    private String usrTelno;
    private String mbtlnum;
    private String brthdy;
    private String ihidnum;
    private String sexdstnCode;
    private String zip;
    private String adresLot;
    private String adres;
    private String detailAdres;
    private String offmTelno;
    private String fxnum;
    private String schoolId;
    private String schoolGb;
    private String schoolNm;
    private Integer schoolLvl;
    private Integer schoolNo;
    private String mberSttus;
    private String mberSttusNm;
    private String sbscrbDe;
    private String secsnDe;
    private String lockAt;
    private String userSe;
    private String chgLastDt;
    private String payBankCode;
    private String payBankCodeNm;
    private String payBank;
    private String holderNm;
    private String groupId;
    private String orgnztId;
    private String indutyCode;
    private String bizrno;
    private String entrpsSeCode;
    private String cxfc;
    private String pstinstCode;
    private String ofcpsNm;
    private String emplNo;
    private String applcntNm;
    private String crtfcDnValue;
    private String crtfcCnValue;
    private String tokenId;
    private Integer lockCnt;
    private String lockLastPnttm;
    private String chgPwdLastPnttm;
    private String terms1Yn;
    private String terms2Yn;
    private String terms3Yn;
    private String terms4Yn;
    private String noticeYn;
    private String eventNotiYn;
    private String mailNotiYn;
    private String smsNotiYn;
    private String certYn;
    private String certDate;
    private String profile;
    private String profileGb;
    private String testYn;
    private String sessionKey;
    private String sessionLimit;
    private String approveDe;
    private String userPic;
    private String citizenYn;
    private String singleYn;
    private String minorYn;
    private String basicYn;
    private String poorYn;
    private String farmYn;
    private String attaFile;
    private String subjectDesc;
    private String profileDesc;
    private String biznoFile;
    /** 신청학원 ID (ARTEDUM.EDU_ESNTL_ID) */
    private String eduEsntlId;
    /** 희망사업(ARTEDUM.EDU_GB): 01=마중물스터디, 02=희망스터디 */
    private String eduGb;
    /** 희망사업명 */
    private String eduGbNm;
    /** 진행상태(ARTEDUM.RUN_STA): 01=임시저장, 02=신청, 03=승인, 04=반려, 05=정지, 99=취소 */
    private String runSta;
    /** 진행상태명 */
    private String runStaNm;
    /** 취급과목 목록 (ARTEDUD SUB_NM, 구분자로 연결) */
    private String subNmList;
}
