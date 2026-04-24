package arami.adminWeb.armbuild.service.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/** 건축물용도 일괄 저장 요청 (items: 등록·수정 대상 배열) */
@Getter
@Setter
public class ArmbuildInsertBatchRequest {

	@NotEmpty(message = "저장할 항목이 없습니다.")
	@Valid
	private List<ArmbuildInsertRequest> items;
}
