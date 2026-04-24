package arami.adminWeb.armbuild.service.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 건축물용도 등록 요청 (JSON Body).
 * BUILD_ID는 서비스에서 DB 채번(getNextBuildId)으로 설정한다.
 * CHG_USER_ID는 서비스에서 로그인 사용자로 설정하며 클라이언트 값은 무시한다.
 */
@Getter
@Setter
public class ArmbuildInsertRequest {

	/** MyBatis/서비스 전용: INSERT 시 BUILD_ID (클라이언트 미전송) */
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
