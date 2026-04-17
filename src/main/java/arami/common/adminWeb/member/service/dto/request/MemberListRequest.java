package arami.common.adminWeb.member.service.dto.request;

import lombok.Data;

/**
 * 관리자 회원 리스트 조회 요청 DTO
 */
@Data
public class MemberListRequest {
    
    // 페이징 파라미터
    private Integer start;        // DataTable 시작 인덱스
    private Integer length;      // DataTable 페이지 크기
    private Integer startIndex;   // 계산된 시작 인덱스
    private Integer lengthPage;   // 계산된 페이지 크기
    
    // 검색 조건 (MyBatis XML 매퍼에서 사용)
    private String searchCondition;  // 검색 조건 ("1": 이름, "2": 아이디, "3": 전화번호)
    private String searchKeyword;    // 검색 키워드
    private String joGunMberSta;     // 가입/회원 상태 필터
    
    // 테이블 필터 조건 (MyBatis XML 매퍼에서 사용)
    private String filterUserId;     // 아이디 필터
    private String filterUserNm;     // 이름 필터
    private String filterEmailAdres;  // 이메일 필터
    private String filterSbscrbDe;   // 가입일 필터
    private String filterMberSttus;  // 회원 상태 필터
    
    // 기존 검색 조건 (필요에 따라 추가)
    private String userId;
    private String userNm;
    private String emailAdres;
    private String mberSttus;
    
    // 기본값 설정
    public void setDefaultPaging() {
        if (this.start == null) {
            this.start = 0;
        }
        if (this.length == null) {
            this.length = 15;
        }
        this.lengthPage = this.length;
        this.startIndex = this.start;
    }
}
