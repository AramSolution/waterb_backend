package arami.common;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

/**
 * 암호화/복호화 대상 필드 정의 DTO
 * 개인정보 암호화 대상 필드를 관리합니다.
 * 
 * @author 아람솔루션
 * @since 2025.01.01
 */
@Data
public class CryptoFields {
	
	/**
	 * 개인정보 암호화 대상 필드 목록
	 */
	public static final List<String> PERSONAL_INFO_FIELDS = Arrays.asList(
		"userNm",      // 관리자명
		"usrTelno",    // 사무실번호
		"mbtlnum",     // 연락처
		"emailAdres"   // 이메일
	);
	
	/**
	 * 필드가 암호화 대상인지 확인
	 * @param fieldName 필드명
	 * @return 암호화 대상이면 true
	 */
	public static boolean isEncryptable(String fieldName) {
		return PERSONAL_INFO_FIELDS.contains(fieldName);
	}
	
	/**
	 * 암호화 대상 필드 배열 반환
	 * @return 필드명 배열
	 */
	public static String[] getFields() {
		return PERSONAL_INFO_FIELDS.toArray(new String[0]);
	}
	
	/**
	 * 암호화 대상 필드 목록 반환
	 * @return 필드명 리스트
	 */
	public static List<String> getPersonalInfoFields() {
		return PERSONAL_INFO_FIELDS;
	}
}
