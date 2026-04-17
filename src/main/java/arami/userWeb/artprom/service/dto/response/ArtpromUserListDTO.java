package arami.userWeb.artprom.service.dto.response;

import lombok.Data;

/**
 * 지원사업 목록 1건 DTO (사용자웹 - selectArtpromList 결과)
 */
@Data
public class ArtpromUserListDTO {

    private String proId;
    private String proGb;
    private String proGbNm;
    /** 주관부서 코드명 (LETTCCMMNDETAILCODE EDR007 / PRO_DEPA) */
    private String proDepaNm;
    /** 사업분야 파이프 구분 값 (예: Y|N|N|N|N) */
    private String proPart;
    /** 내 신청현황용: ARTAPPM.REQ_ID (신청 PK, 상세/수정 링크 식별 - MY PAGE reqId 기반 로딩용) */
    private String reqId;
    /** 대상 구분 (E|J|H|U|T|Y1|O1 등) */
    private String proTarget;
    private String proType1;
    private String proType2;
    private String proType3;
    private String proType4;
    private String proType5;
    private String proType6;
    private String proType7;
    /** 교육/지원사업 등 */
    private String proType;
    private String proNm;
    /** 대상명 */
    private String proTargetNm;
    /** 기타명 */
    private String etcNm;
    private String recFromDd;
    private String recToDd;
    private String proSum;
    /** 메인 목록용: 지원사업(공고) 진행상태 (01=공고, 02=접수중, 03=검토중, 04=진행, 05=완료, 99=취소) */
    private String runSta;
    /** 메인 목록용: 진행상태명 */
    private String runStaNm;
    /** 현재 로그인 사용자 기준 즐겨찾기 여부 (Y/N). */
    private String favoriteYn;
    /** 내 신청현황용: 신청 상태 코드 (01=임시저장, 02=신청, …) */
    private String appSttusCode;
    /** 내 신청현황용: 신청 상태명 */
    private String appSttusCodeNm;
    /** 내 신청현황용: ARTAPPM.PRO_SEQ (신청 순번, 상세/수정 링크 식별) */
    private String proSeq;
    /** 내 신청현황용: ARTAPPM.REQ_ESNTL_ID (자녀/신청자 ID, 상세/수정 링크 식별) */
    private String reqEsntlId;
    /** 내 신청현황용: 신청자(학생)명 (ARMUSER.USER_NM) */
    private String reqUserNm;
    /** 내 신청현황용: 선정여부 (ARTAPPM.RESULT_GB) N=미선정, Y=선정, R=예비 */
    private String resultGb;
}
