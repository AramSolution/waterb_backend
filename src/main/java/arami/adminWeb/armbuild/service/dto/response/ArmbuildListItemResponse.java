package arami.adminWeb.armbuild.service.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 건축물용도(ARMBULD) 목록 한 행 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArmbuildListItemResponse {

	private String buildId;
	private String gubun1;
	private String gubun2;
	private String buildNm;
	private BigDecimal dayVal;
	private String buildDesc;
	private String sttusCode;
	private String chgUserId;
	private String crtDate;
	private String chgDate;
}
