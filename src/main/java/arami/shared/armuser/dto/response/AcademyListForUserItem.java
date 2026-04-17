package arami.shared.armuser.dto.response;

import lombok.Data;

/**
 * 사용자웹 학원조회 목록 1건 DTO
 * 정상 학원(ANR, P) + 과목(있으면 콤마, 없으면 공백)
 */
@Data
public class AcademyListForUserItem {

    private String esntlId;
    private String userNm;
    private String schoolNm;
    private String adres;
    private String detailAdres;
    private String offmTelno;
    private String usrTelno;
    /** 취급과목 (ARTEDUD SUB_NM 콤마 구분, 없으면 공백) */
    private String subNmList;
    /** 학원 로고 (USER_PIC: ARTFILE fileId) */
    private String userPic;
    /** 학원 로고 파일 SEQ (ARTFILE SEQ, view URL에 필요) */
    private Integer userPicSeq;
    /** 학원 소개 (PROFILE_DESC) */
    private String profileDesc;
}
