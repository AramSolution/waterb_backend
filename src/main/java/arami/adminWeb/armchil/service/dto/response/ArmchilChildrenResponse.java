package arami.adminWeb.armchil.service.dto.response;

import arami.shared.armchil.dto.response.ArmchilChildDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 관리자웹 - 부모별 자녀 목록 조회 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArmchilChildrenResponse {

    private List<ArmchilChildDTO> data;
    private String result;
}
