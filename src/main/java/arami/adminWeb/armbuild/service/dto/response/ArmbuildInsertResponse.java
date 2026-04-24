package arami.adminWeb.armbuild.service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/** 건축물용도 등록 결과 (result/message + 채번된 buildId) */
@Data
@NoArgsConstructor
public class ArmbuildInsertResponse {

	private String result;
	private String message;
	private String buildId;
}
