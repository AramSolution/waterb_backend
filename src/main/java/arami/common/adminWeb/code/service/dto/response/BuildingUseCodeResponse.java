package arami.common.adminWeb.code.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 건축용도 코드 한 행 (중분류 WAT001 + 소분류 WAT002 조인)
 * selectBuildingUseCodeList 결과 매핑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingUseCodeResponse {

	private String midCode;
	private String midName;
	private String subCode;
	private String subName;
}
