package arami.common.adminWeb.code.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 소분류코드 한 건 응답 DTO (API 응답용)
 * selectCmmDetailCodeListByCategory 결과 CODE_CATEGORY, CODE_ID, CODE, CODE_NM, CODE_DC 매핑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailCodeListByCategoryResponse {

    private String codeCategory;
    private String codeId;
    private String code;
    private String codeNm;
    private String codeDc;
}
