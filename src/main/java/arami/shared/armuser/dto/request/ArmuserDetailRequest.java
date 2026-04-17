package arami.shared.armuser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ARMUSER 상세 조회 요청 DTO (고유ID)
 */
@Data
public class ArmuserDetailRequest {

    @NotBlank
    private String esntlId;
}
