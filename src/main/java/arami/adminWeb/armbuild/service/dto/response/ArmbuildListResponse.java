package arami.adminWeb.armbuild.service.dto.response;

import java.util.List;

import lombok.Data;

/** 건축물용도 목록 조회 응답 */
@Data
public class ArmbuildListResponse {

	private List<ArmbuildListItemResponse> data;
	private String result;
	private String message;
}
