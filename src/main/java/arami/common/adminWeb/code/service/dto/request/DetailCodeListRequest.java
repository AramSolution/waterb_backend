package arami.common.adminWeb.code.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 소분류코드 리스트 조회 파라미터 DTO (MyBatis parameterType용)
 * selectLetDetailCodeListDto 쿼리 #{codeId} 매핑
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailCodeListRequest {

    private String codeId;
}
