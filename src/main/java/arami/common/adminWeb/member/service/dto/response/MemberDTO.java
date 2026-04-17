package arami.common.adminWeb.member.service.dto.response;

import lombok.Data;

/**
 * 관리자 회원 정보 DTO
 */
@Data
public class MemberDTO {
    
    private String rnum;         // 번호(ROW_NUMBER)
    private String esntlId;      // 고유 ID
    private String userId;       // 사용자 ID
    private String userNm;       // 사용자 이름
    private String emailAdres;   // 이메일 주소
    private String usrTelno;     // 사무실전화번호
    private String mbtlnum;      // 휴대전화번호
    private String mberSttus;    // 회원 상태
    private String mberSttusNm;  // 회원 상태명
    private String userSe;       // 회원 종류 코드
    private String sbscrbDe;     // 가입일자
    private String secsnDe;      // 탈퇴일자
    private String lockAt;       // 잠금여부
    private String lockLastPnttm; // 잠금일시
    private String groupId;      // 권한 그룹 ID
    
    // 기타 필드 (필요에 따라 추가)
    // private String telNo;
    // private String brthdy;
    // private String detailAdres;
}
