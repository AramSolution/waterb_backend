package arami.adminWeb.armbuild.service.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 건축물용도 등록·수정 요청 (일괄 저장 시 items 원소).
 * buildId가 비어 있으면 신규 INSERT(서버 채번), 값이 있으면 기존 건 UPDATE(STTUS_CODE='A'인 행만).
 * CHG_USER_ID는 서비스에서 로그인 사용자로 설정한다.
 */
@Getter
@Setter
public class ArmbuildInsertRequest {

	@Size(max = 20)
	private String buildId;

	@NotBlank
	@Size(max = 2)
	private String gubun1;

	@NotBlank
	@Size(max = 4)
	private String gubun2;

	@NotBlank
	@Size(max = 256)
	private String buildNm;

	@Digits(integer = 4, fraction = 1)
	private BigDecimal dayVal;

	@Size(max = 2048)
	private String buildDesc;

	/** 서비스 전용: MyBatis 매핑용, API 입력으로 사용하지 않음 */
	private String chgUserId;
}
