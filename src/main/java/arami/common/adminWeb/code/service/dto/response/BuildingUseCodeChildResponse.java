package arami.common.adminWeb.code.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 건축용도 소분류 한 건 (트리의 children 요소) */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingUseCodeChildResponse {

	private String code;
	private String name;
}
