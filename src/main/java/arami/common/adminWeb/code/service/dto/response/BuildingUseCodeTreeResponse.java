package arami.common.adminWeb.code.service.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 건축용도 중분류 + 소분류 children (프론트용 트리 한 노드) */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingUseCodeTreeResponse {

	private String code;
	private String name;
	@Builder.Default
	private List<BuildingUseCodeChildResponse> children = new ArrayList<>();
}
