package arami.adminWeb.armbuild.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 건축물용도 목록 조회 — building-use-codes 중분류·소분류 code와 동일 값으로 GUBUN1, GUBUN2 매칭
 */
@Getter
@Setter
public class ArmbuildListRequest {

	@NotBlank
	private String gubun1;

	@NotBlank
	private String gubun2;
}
