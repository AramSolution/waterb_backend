package arami.member.dto;

import arami.common.cipher.LeaAttributeConverter;
import jakarta.persistence.Convert;
import lombok.Data;

/**
 * 관리자 회원 정보 DTO
 * 개인정보 필드는 @Convert를 사용하여 자동 암호화/복호화 처리
 * 
 * - LeaAttributeConverter가 자동으로 암호화/복호화 처리
 * - 필드 자체에 암호화된 값을 저장 (DB 저장용)
 * - getter 호출 시 LeaAttributeConverter를 통해 복호화된 평문 값 반환
 * - setter 호출 시 LeaAttributeConverter를 통해 암호화하여 저장
 * 
 * @author 아람솔루션
 * @since 2025.01.01
 */
@Data
public class AdminUserMemberDTO {
    
    private String esntlId;          // 고유ID
    private String userId;           // 사용자ID
    private String password;         // 비밀번호
    private String newPw;             // 새 비밀번호
    
    @Convert(converter = LeaAttributeConverter.class)
    private String userNm;            // 관리자명 (암호화)
    
    @Convert(converter = LeaAttributeConverter.class)
    private String usrTelno;          // 사무실번호 (암호화)
    
    @Convert(converter = LeaAttributeConverter.class)
    private String mbtlnum;           // 연락처 (암호화)
    
    @Convert(converter = LeaAttributeConverter.class)
    private String emailAdres;        // 이메일 (암호화)
    
    private String mberSttus;         // 회원상태
    private String mberSttusNm;       // 회원상태명
    private String sbscrbDe;          // 가입일자
    private String secsnDe;           // 탈퇴일자
    private String lockAt;             // 잠금여부
    private String lockLastPnttm;     // 잠금일시
    private String groupId;            // 그룹ID
    private String userSe;            // 사용자구분
    private String chgLastDt;         // 최종수정일시
    
    // 검색 조건
    private String searchCondition;   // 검색조건655
    private String searchKeyword;     // 검색키워드
    private String joGunMberSta;      // 가입상태
    private Integer startIndex;       // 시작인덱스
    private Integer lengthPage;       // 페이지크기
    private Integer start;            // 시작
    private Integer length;           // 길이
    
    // LeaAttributeConverter 인스턴스
    private static final LeaAttributeConverter converter = new LeaAttributeConverter();
    
    /**
     * userNm getter - LeaAttributeConverter를 통해 복호화된 평문 값 반환
     */
    public String getUserNm() {
        if (userNm == null || userNm.isEmpty()) {
            return userNm;
        }
        try {
            return converter.convertToEntityAttribute(userNm);
        } catch (Exception e) {
            // 복호화 실패 시 원본 반환 (이미 평문일 수 있음)
            return userNm;
        }
    }
    
    /**
     * userNm setter - LeaAttributeConverter를 통해 암호화하여 저장
     * Controller에서 평문 값이 들어오면 암호화하여 저장
     * MyBatis에서 암호화된 값이 들어오면 그대로 저장 (이미 암호화된 값)
     */
    public void setUserNm(String userNm) {
        if (userNm == null || userNm.isEmpty()) {
            this.userNm = userNm;
            return;
        }
        try {
            // 이미 암호화된 값인지 확인
            if (isEncrypted(userNm)) {
                // MyBatis에서 조회된 암호화된 값 → 그대로 저장
                this.userNm = userNm;
            } else {
                // Controller에서 입력된 평문 값 → 암호화하여 저장
                this.userNm = converter.convertToDatabaseColumn(userNm);
            }
        } catch (Exception e) {
            // 암호화 실패 시 원본 저장
            this.userNm = userNm;
        }
    }
    
    /**
     * usrTelno getter - LeaAttributeConverter를 통해 복호화된 평문 값 반환
     */
    public String getUsrTelno() {
        if (usrTelno == null || usrTelno.isEmpty()) {
            return usrTelno;
        }
        try {
            return converter.convertToEntityAttribute(usrTelno);
        } catch (Exception e) {
            return usrTelno;
        }
    }
    
    /**
     * usrTelno setter - LeaAttributeConverter를 통해 암호화하여 저장
     */
    public void setUsrTelno(String usrTelno) {
        if (usrTelno == null || usrTelno.isEmpty()) {
            this.usrTelno = usrTelno;
            return;
        }
        try {
            if (isEncrypted(usrTelno)) {
                this.usrTelno = usrTelno;
            } else {
                this.usrTelno = converter.convertToDatabaseColumn(usrTelno);
            }
        } catch (Exception e) {
            this.usrTelno = usrTelno;
        }
    }
    
    /**
     * mbtlnum getter - LeaAttributeConverter를 통해 복호화된 평문 값 반환
     */
    public String getMbtlnum() {
        if (mbtlnum == null || mbtlnum.isEmpty()) {
            return mbtlnum;
        }
        try {
            return converter.convertToEntityAttribute(mbtlnum);
        } catch (Exception e) {
            return mbtlnum;
        }
    }
    
    /**
     * mbtlnum setter - LeaAttributeConverter를 통해 암호화하여 저장
     */
    public void setMbtlnum(String mbtlnum) {
        if (mbtlnum == null || mbtlnum.isEmpty()) {
            this.mbtlnum = mbtlnum;
            return;
        }
        try {
            if (isEncrypted(mbtlnum)) {
                this.mbtlnum = mbtlnum;
            } else {
                this.mbtlnum = converter.convertToDatabaseColumn(mbtlnum);
            }
        } catch (Exception e) {
            this.mbtlnum = mbtlnum;
        }
    }
    
    /**
     * emailAdres getter - LeaAttributeConverter를 통해 복호화된 평문 값 반환
     */
    public String getEmailAdres() {
        if (emailAdres == null || emailAdres.isEmpty()) {
            return emailAdres;
        }
        try {
            return converter.convertToEntityAttribute(emailAdres);
        } catch (Exception e) {
            return emailAdres;
        }
    }
    
    /**
     * emailAdres setter - LeaAttributeConverter를 통해 암호화하여 저장
     */
    public void setEmailAdres(String emailAdres) {
        if (emailAdres == null || emailAdres.isEmpty()) {
            this.emailAdres = emailAdres;
            return;
        }
        try {
            if (isEncrypted(emailAdres)) {
                this.emailAdres = emailAdres;
            } else {
                this.emailAdres = converter.convertToDatabaseColumn(emailAdres);
            }
        } catch (Exception e) {
            this.emailAdres = emailAdres;
        }
    }
    
    /**
     * 값이 이미 암호화된 값인지 확인 (Base64 형식 체크)
     */
    private boolean isEncrypted(String value) {
        if (value == null || value.isEmpty() || value.length() < 20) {
            return false;
        }
        try {
            java.util.Base64.getDecoder().decode(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
