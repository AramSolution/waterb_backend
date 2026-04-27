package arami.common.adminWeb.code.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 건축용도 코드 구분별 기준단가 조회 파라미터 DTO (MyBatis parameterType용)
 * selectBuildingUseCodeUnitPrice 쿼리 #{isOtherAct} 매핑
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingUseCodeUnitPriceRequest {

    private Boolean isOtherAct;
}
