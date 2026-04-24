package arami.adminWeb.armbuild.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 건축물용도 논리 삭제(STTUS_CODE = 'D') — BUILD_ID만 사용 */
@Getter
@AllArgsConstructor
public class ArmbuildDeleteParam {

	private final String buildId;
}
