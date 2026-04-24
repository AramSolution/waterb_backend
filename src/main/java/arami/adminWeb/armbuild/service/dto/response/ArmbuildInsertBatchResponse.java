package arami.adminWeb.armbuild.service.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/** 건축물용도 일괄 저장 결과 (입력 items 순서와 동일한 buildIds) */
@Data
@NoArgsConstructor
public class ArmbuildInsertBatchResponse {

	private String result;
	private String message;
	private List<String> buildIds = new ArrayList<>();
}
