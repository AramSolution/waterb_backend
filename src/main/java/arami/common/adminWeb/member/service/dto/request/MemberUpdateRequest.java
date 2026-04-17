package arami.common.adminWeb.member.service.dto.request;

import lombok.Data;

/**
 * 관리자 회원 수정 요청 DTO
 */
@Data
public class MemberUpdateRequest {
    
    // 필수 필드
    private String esntlId;      // 고유 ID (수정 대상 식별)
    private String userId;       // 사용자 ID
    
    // 수정 가능한 필드
    private String password;     // 비밀번호 (선택적, 변경 시에만)
    private String userNm;       // 사용자 이름
    private String emailAdres;   // 이메일 주소
    private String mberSttus;    // 회원 상태

    // 연락처/부가정보 (MyBatis 매퍼에서 사용)
    private String usrTelno;     // 유선전화
    private String mbtlnum;      // 휴대전화
    private String sbscrbDe;     // 가입일(문자열 yyyy-MM-dd 등)
    private String lockAt;       // 잠금여부(Y/N)
    private String groupId;      // 그룹ID
    
    // 시스템 필드
    private String newPw;        // 암호화된 비밀번호 (password가 있을 때만 설정)
    
    // 기타 필드 (필요에 따라 추가)
}
