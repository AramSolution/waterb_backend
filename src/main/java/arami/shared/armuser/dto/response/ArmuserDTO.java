package arami.shared.armuser.dto.response;

import lombok.Data;

/**
 * ARMUSER 정보 DTO (목록/상세 공통)
 */
@Data
public class ArmuserDTO {

    /** 목록/엑셀 조회 시 행 번호 (RNUM) */
    private Integer rnum;
    private String esntlId;
    private String userId;
    private String userNm;
    private String emailAdres;
    private String usrTelno;
    private String mbtlnum;
    /** 생년월일 (YYYY-MM-DD) */
    private String brthdy;
    /** 주민등록번호 */
    private String ihidnum;
    /** 성별코드 (M/F 등) */
    private String sexdstnCode;
    /** 우편번호 */
    private String zip;
    /** 지번주소 (복호화) */
    private String adresLot;
    /** 주소 */
    private String adres;
    /** 상세주소 */
    private String detailAdres;
    /** 사무실전화 (복호화) */
    private String offmTelno;
    /** 회원팩스 (복호화) */
    private String fxnum;
    /** 학교 ID */
    private String schoolId;
    /** 학교구분 */
    private String schoolGb;
    /** 학교명 */
    private String schoolNm;
    /** 학년 */
    private Integer schoolLvl;
    /** 반 */
    private Integer schoolNo;
    private String mberSttus;
    private String mberSttusNm;
    private String sbscrbDe;
    private String secsnDe;
    private String lockAt;
    private String userSe;
    private String chgLastDt;
    /** 은행코드 (ARM002) */
    private String payBankCode;
    /** 은행명 */
    private String payBankCodeNm;
    /** 계좌번호 (복호화) */
    private String payBank;
    /** 예금주명 (복호화) */
    private String holderNm;

    // ----- 그룹/조직/기업 -----
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

    // ----- 인증/잠금 -----
    private String crtfcDnValue;
    private String crtfcCnValue;
    private String tokenId;
    private Integer lockCnt;
    private String lockLastPnttm;
    private String chgPwdLastPnttm;

    // ----- 약관/알림 -----
    private String terms1Yn;
    private String terms2Yn;
    private String terms3Yn;
    private String terms4Yn;
    private String noticeYn;
    private String eventNotiYn;
    private String mailNotiYn;
    private String smsNotiYn;

    // ----- 인증/프로필/세션 -----
    private String certYn;
    private String certDate;
    private String profile;
    private String profileGb;
    private String testYn;
    private String sessionKey;
    private String sessionLimit;
    private String approveDe;
    private String userPic;

    // ----- 기타 여부 -----
    private String citizenYn;
    private String singleYn;
    private String minorYn;
    private String basicYn;
    private String poorYn;
    /** 저소득층 표시 (기초생활수급자/차상위계층/공백) */
    private String lowIncomeNm;
    private String farmYn;
    /** 부모관계 (RELATION_GB) */
    private String relationGb;
    /** 부모관계명 (RELATION_GB_NM: F=부, M=모, E=기타) */
    private String relationGbNm;

    // ----- 첨부/소개 -----
    private String attaFile;
    private String subjectDesc;
    private String profileDesc;
    private String biznoFile;
    /** 학교소재지 (FARM_DESC) */
    private String farmDesc;

    // ----- OAuth 연동 -----
    /** OAUTH_GB (일반=00, 네이버=01, 카카오=02) */
    private String oauthGb;
    /** 네이버 OAuth 고유ID (ARMUSER.NAVER_AUTH_ID) */
    private String naverAuthId;
    /** 카카오 OAuth 고유ID (ARMUSER.KAKAO_AUTH_ID) */
    private String kakaoAuthId;
}
