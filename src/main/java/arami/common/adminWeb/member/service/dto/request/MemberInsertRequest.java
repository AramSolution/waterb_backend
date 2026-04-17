package arami.common.adminWeb.member.service.dto.request;

import lombok.Data;

/**
 * 관리자 회원 등록 요청 DTO
 */
@Data
public class MemberInsertRequest {
    
    // 회원 기본 정보
    private String userId;        // 사용자 ID
    private String password;      // 비밀번호 (암호화 전)
    private String userNm;        // 사용자 이름
    private String emailAdres;    // 이메일 주소
    private String mberSttus;     // 회원 상태

    // 연락처/부가정보 (MyBatis 매퍼에서 사용)
    private String usrTelno;      // 유선전화
    private String mbtlnum;       // 휴대전화
    private String sbscrbDe;      // 가입일(문자열 yyyy-MM-dd 등)
    private String groupId;       // 그룹ID
    
    // 시스템 생성 필드 (등록 시 자동 생성)
    private String esntlId;       // 고유 ID (코드 채번)
    private String newPw;         // 암호화된 비밀번호
    private String userSe;        // 회원 종류 코드 (기본값: "USR")
    
    // 기타 필드 (필요에 따라 추가)
    // private String telNo;
    // private String brthdy;
    // private String detailAdres;
}
