package arami.common.adminWeb.code.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 소분류코드 리스트 조회 파라미터 DTO (MyBatis parameterType용)
 * selectCmmDetailCodeListByCategory 쿼리 #{codeId}, #{studentCode} 매핑
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailCodeListByCategoryRequest {

    private String codeId;
    private String studentCode;
}
