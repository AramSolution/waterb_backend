package arami.shared.armuser.dto.request;

import lombok.Data;

/**
 * ARMUSER 수정 요청 DTO (공통)
 */
@Data
public class ArmuserUpdateRequest {

    /** 고유ID (필수) */
    private String esntlId;
    /** 회원종류 */
    private String userSe;
    /** 회원ID */
    private String userId;
    /** 비밀번호 (변경 시에만 값 전달) */
    private String password;
    /** 비밀번호힌트 */
    private String passwordHint;
    /** 비밀번호정답 */
    private String passwordCnsr;
    /** 회원명 */
    private String userNm;
    /** 주민(법인)등록번호 */
    private String ihidnum;
    /** 전화번호 */
    private String usrTelno;
    /** 이동전화번호 */
    private String mbtlnum;
    /** 이메일주소 */
    private String emailAdres;
    /** 우편번호 */
    private String zip;
    /** 지번주소 */
    private String adresLot;
    /** 주소 */
    private String adres;
    /** 상세주소 */
    private String detailAdres;
    /** 회원팩스번호 */
    private String fxnum;
    /** 사무실전화번호 */
    private String offmTelno;
    /** 생년월일 (YYYYMMDD) */
    private String brthdy;
    /** 성별코드 */
    private String sexdstnCode;
    /** 회원상태 (A:대기, P:사용, D:탈퇴) */
    private String mberSttus;
    /** 가입일자 */
    private String sbscrbDe;
    /** 탈퇴일자 (관리자용) */
    private String secsnDe;
    /** 잠금여부 */
    private String lockAt;
    /** OPEN API 학교ID */
    private String schoolId;
    /** 학교코드 */
    private String schoolGb;
    /** 학교명 */
    private String schoolNm;
    /** 학년 */
    private Integer schoolLvl;
    /** 반 */
    private Integer schoolNo;
    /** 은행코드 */
    private String payBankCode;
    /** 계좌번호 */
    private String payBank;
    /** 예금주 */
    private String holderNm;
    /** 한부모가족여부 */
    private String singleYn;
    /** 기초생활수급자여부 */
    private String basicYn;
    /** 차상위계층여부 */
    private String poorYn;
    /** 인증여부 */
    private String certYn;
    /** 보호자 인증 시 수신한 DI(개인식별코드) - ARMUSER.CRTFC_DN_VALUE, 값 있을 때만 업데이트 */
    private String crtfcDnValue;
    /** 약관/알림 동의 */
    private String terms1Yn;
    private String terms2Yn;
    private String terms3Yn;
    private String terms4Yn;
    private String noticeYn;
    private String eventNotiYn;
    private String mailNotiYn;
    private String smsNotiYn;
    /** 그룹ID */
    private String groupId;
    /** 사업자등록번호 */
    private String bizrno;
    /** 기업구분코드 */
    private String entrpsSeCode;
    /** 대표이사 */
    private String cxfc;
    /** 소속기관코드 */
    private String pstinstCode;
    /** 조직ID */
    private String orgnztId;
    /** 업종코드 */
    private String indutyCode;
    /** 직위명 */
    private String ofcpsNm;
    /** 사원번호 */
    private String emplNo;
    /** 신청인명 */
    private String applcntNm;
    /** 인증일시 */
    private String certDate;
    /** 프로필 */
    private String profile;
    /** 사진구분 */
    private String profileGb;
    /** 테스트회원 */
    private String testYn;
    /** 세션키 */
    private String sessionKey;
    /** 세션최대보유일시 */
    private String sessionLimit;
    /** 승인일자 */
    private String approveDe;
    /** 사진(로고) */
    private String userPic;
    /** 시민인증여부 */
    private String citizenYn;
    /** 14세미만여부 */
    private String minorYn;
    /** 농어촌학교여부 */
    private String farmYn;
    /** 첨부파일 */
    private String attaFile;
    /** 취급과목 */
    private String subjectDesc;
    /** 학원소개 */
    private String profileDesc;
    /** 사업자등록증 */
    private String biznoFile;
    /** 부모관계 (RELATION_GB, 학부모 시 사용) */
    private String relationGb;
    /** 학교소재지 (FARM_DESC, 학생 시 사용) */
    private String farmDesc;
}
