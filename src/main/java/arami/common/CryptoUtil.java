package arami.common;

import java.util.Base64;

import egovframework.com.cmm.service.EgovProperties;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.crypto.EgovCryptoService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * 개인정보 암호화/복호화 공통 유틸리티 클래스
 * LEA 암호화 방식을 사용합니다.
 * 
 * @author 아람솔루션
 * @since 2025.01.01
 */
@Slf4j
@Component
public class CryptoUtil {

	@Resource(name = "egovARIACryptoService")
	private EgovCryptoService cryptoService;
	
	private static String algoritmKey;
	
	@PostConstruct
	public void init() {
		try {
			algoritmKey = EgovProperties.getProperty("Globals.crypto.algoritm");
		} catch (Exception e) {
			log.error("암호화 키를 가져오는 중 오류 발생: " + e.getMessage(), e);
		}
	}
	
	/**
	 * CryptoService 인스턴스를 반환합니다
	 * @return EgovCryptoService
	 */
	private EgovCryptoService getCryptoService() {
		return cryptoService;
	}
	
	/**
	 * 문자열을 암호화합니다 (LEA 방식)
	 * @param plainText 암호화할 평문
	 * @return 암호화된 문자열 (Base64 인코딩)
	 * @throws Exception
	 */
	public String encrypt(String plainText) throws Exception {
		if (plainText == null || plainText.isEmpty()) {
			return "";
		}
		
		if (getCryptoService() == null) {
			throw new IllegalStateException("CryptoService가 초기화되지 않았습니다.");
		}
		
		if (algoritmKey == null || algoritmKey.isEmpty()) {
			throw new IllegalStateException("암호화 키가 설정되지 않았습니다. Globals.crypto.algoritm 속성을 확인하세요.");
		}
		
		try {
			byte[] encryptedBytes = getCryptoService().encrypt(plainText.getBytes("UTF-8"), algoritmKey);
			// 암호화된 바이트 배열을 Base64로 인코딩하여 문자열로 변환
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			log.error("암호화 중 오류 발생: " + e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 암호화된 문자열을 복호화합니다 (LEA 방식)
	 * @param encryptedText 암호화된 문자열 (Base64 인코딩)
	 * @return 복호화된 평문
	 * @throws Exception
	 */
	public String decrypt(String encryptedText) throws Exception {
		if (encryptedText == null || encryptedText.isEmpty()) {
			return "";
		}
		
		if (getCryptoService() == null) {
			throw new IllegalStateException("CryptoService가 초기화되지 않았습니다.");
		}
		
		if (algoritmKey == null || algoritmKey.isEmpty()) {
			throw new IllegalStateException("암호화 키가 설정되지 않았습니다. Globals.crypto.algoritm 속성을 확인하세요.");
		}
		
		try {
			// Base64로 인코딩된 문자열을 디코딩하여 바이트 배열로 변환
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
			byte[] decryptedBytes = getCryptoService().decrypt(encryptedBytes, algoritmKey);
			return new String(decryptedBytes, "UTF-8");
		} catch (Exception e) {
			log.error("복호화 중 오류 발생: " + e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ModelMap의 특정 필드들을 암호화합니다
	 * @param model ModelMap
	 * @param fields 암호화할 필드명 배열
	 * @throws Exception
	 */
	public void encryptFields(org.springframework.ui.ModelMap model, String... fields) throws Exception {
		if (model == null || fields == null) {
			return;
		}
		
		for (String field : fields) {
			Object value = model.get(field);
			if (value != null && !value.toString().isEmpty()) {
				String encrypted = encrypt(value.toString());
				model.put(field, encrypted);
			}
		}
	}
	
	/**
	 * ModelMap의 암호화 대상 필드를 자동으로 감지하여 암호화합니다
	 * CryptoFields에 정의된 필드만 암호화합니다.
	 * @param model ModelMap
	 * @throws Exception
	 */
	public void encryptPersonalInfo(org.springframework.ui.ModelMap model) throws Exception {
		if (model == null) {
			return;
		}
		
		for (String field : CryptoFields.PERSONAL_INFO_FIELDS) {
			Object value = model.get(field);
			if (value != null && !value.toString().isEmpty()) {
				try {
					String encrypted = encrypt(value.toString());
					model.put(field, encrypted);
				} catch (Exception e) {
					// 암호화 실패 시 원본 값 유지
				}
			}
		}
	}
	
	/**
	 * ModelMap의 특정 필드들을 복호화합니다
	 * @param model ModelMap
	 * @param fields 복호화할 필드명 배열
	 * @throws Exception
	 */
	public void decryptFields(org.springframework.ui.ModelMap model, String... fields) throws Exception {
		if (model == null || fields == null) {
			return;
		}
		
		for (String field : fields) {
			Object value = model.get(field);
			if (value != null && !value.toString().isEmpty()) {
				try {
					String decrypted = decrypt(value.toString());
					model.put(field, decrypted);
				} catch (Exception e) {
					log.warn("필드 [" + field + "] 복호화 실패 (이미 평문일 수 있음): " + e.getMessage());
					// 복호화 실패 시 원본 값 유지
				}
			}
		}
	}
	
	/**
	 * EgovMap의 특정 필드들을 복호화합니다
	 * @param map EgovMap
	 * @param fields 복호화할 필드명 배열
	 * @throws Exception
	 */
	public void decryptEgovMap(org.egovframe.rte.psl.dataaccess.util.EgovMap map, String... fields) throws Exception {
		if (map == null || fields == null) {
			return;
		}
		
		for (String field : fields) {
			Object value = map.get(field);
			if (value != null && !value.toString().isEmpty()) {
				try {
					String decrypted = decrypt(value.toString());
					map.put(field, decrypted);
				} catch (Exception e) {
					// 복호화 실패 시 원본 값 유지
				}
			}
		}
	}
	
	/**
	 * EgovMap의 암호화 대상 필드를 자동으로 감지하여 복호화합니다
	 * CryptoFields에 정의된 필드만 복호화합니다.
	 * @param map EgovMap
	 * @throws Exception
	 */
	public void decryptPersonalInfo(org.egovframe.rte.psl.dataaccess.util.EgovMap map) throws Exception {
		if (map == null) {
			return;
		}
		
		for (String field : CryptoFields.PERSONAL_INFO_FIELDS) {
			Object value = map.get(field);
			if (value != null && !value.toString().isEmpty()) {
				try {
					String decrypted = decrypt(value.toString());
					map.put(field, decrypted);
				} catch (Exception e) {
					// 복호화 실패 시 원본 값 유지
				}
			}
		}
	}
	
	/**
	 * Map의 암호화 대상 필드를 자동으로 감지하여 복호화합니다
	 * CryptoFields에 정의된 필드만 복호화합니다.
	 * @param map Map<String, Object> (EgovMap 제외)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void decryptPersonalInfoMap(java.util.Map<String, Object> map) throws Exception {
		if (map == null) {
			return;
		}
		
		// EgovMap인 경우 별도 메서드 호출
		if (map instanceof org.egovframe.rte.psl.dataaccess.util.EgovMap) {
			decryptPersonalInfo((org.egovframe.rte.psl.dataaccess.util.EgovMap) map);
			return;
		}
		
		for (String field : CryptoFields.PERSONAL_INFO_FIELDS) {
			Object value = map.get(field);
			if (value != null && !value.toString().isEmpty()) {
				try {
					String decrypted = decrypt(value.toString());
					map.put(field, decrypted);
				} catch (Exception e) {
					// 복호화 실패 시 원본 값 유지
				}
			}
		}
	}
	
	/**
	 * LoginVO 객체의 암호화 대상 필드를 자동으로 감지하여 복호화합니다
	 * CryptoFields에 정의된 필드만 복호화합니다.
	 * LoginVO 필드명 매핑: name -> userNm, email -> emailAdres
	 * @param loginVO LoginVO 객체
	 * @throws Exception
	 */
	public void decryptPersonalInfo(egovframework.com.cmm.LoginVO loginVO) throws Exception {
		if (loginVO == null) {
			return;
		}
		
		// LoginVO 필드명 매핑 (CryptoFields 필드명 -> LoginVO 필드명)
		// name 필드 복호화 (userNm에 해당)
		if (loginVO.getName() != null && !loginVO.getName().isEmpty()) {
			try {
				String decrypted = decrypt(loginVO.getName());
				loginVO.setName(decrypted);
			} catch (Exception e) {
				// 복호화 실패 시 원본 값 유지
			}
		}
		
		// email 필드 복호화 (emailAdres에 해당)
		if (loginVO.getEmail() != null && !loginVO.getEmail().isEmpty()) {
			try {
				String decrypted = decrypt(loginVO.getEmail());
				loginVO.setEmail(decrypted);
			} catch (Exception e) {
				// 복호화 실패 시 원본 값 유지
			}
		}
		
		// LoginVO에는 usrTelno, mbtlnum 필드가 없으므로 처리하지 않음
	}
	
	/**
	 * List<EgovMap>의 각 항목에서 특정 필드들을 복호화합니다
	 * @param list EgovMap 리스트
	 * @param fields 복호화할 필드명 배열
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void decryptEgovMapList(java.util.List<?> list, String... fields) throws Exception {
		if (list == null || fields == null) {
			return;
		}
		
		for (Object item : list) {
			if (item instanceof org.egovframe.rte.psl.dataaccess.util.EgovMap) {
				decryptEgovMap((org.egovframe.rte.psl.dataaccess.util.EgovMap) item, fields);
			} else if (item instanceof java.util.Map) {
				// 일반 Map도 처리
				java.util.Map<String, Object> map = (java.util.Map<String, Object>) item;
				for (String field : fields) {
					Object value = map.get(field);
					if (value != null && !value.toString().isEmpty()) {
						try {
							String decrypted = decrypt(value.toString());
							map.put(field, decrypted);
						} catch (Exception e) {
							// 복호화 실패 시 원본 값 유지
						}
					}
				}
			}
		}
	}
	
	/**
	 * List<EgovMap>의 각 항목에서 암호화 대상 필드를 자동으로 감지하여 복호화합니다
	 * CryptoFields에 정의된 필드만 복호화합니다.
	 * @param list EgovMap 리스트
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void decryptPersonalInfoList(java.util.List<?> list) throws Exception {
		if (list == null) {
			return;
		}
		
		for (Object item : list) {
			if (item instanceof org.egovframe.rte.psl.dataaccess.util.EgovMap) {
				decryptPersonalInfo((org.egovframe.rte.psl.dataaccess.util.EgovMap) item);
			} else if (item instanceof java.util.Map) {
				// 일반 Map도 처리
				decryptPersonalInfoMap((java.util.Map<String, Object>) item);
			}
		}
	}
}
