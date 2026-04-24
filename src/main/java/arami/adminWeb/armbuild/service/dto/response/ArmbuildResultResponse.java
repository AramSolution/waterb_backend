package arami.adminWeb.armbuild.service.dto.response;

import lombok.Data;

/** 건축물용도 API 공통 결과 (ArtappsResultResponse와 동일 필드) */
@Data
public class ArmbuildResultResponse {

	private String result;
	private String message;
}
