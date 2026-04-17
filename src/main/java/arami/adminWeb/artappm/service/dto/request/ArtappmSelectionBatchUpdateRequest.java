package arami.adminWeb.artappm.service.dto.request;

import jakarta.validation.Valid;
import java.util.List;
import lombok.Data;

/**
 * 선정관리 선정여부 일괄 변경 요청 DTO (목록 래퍼)
 */
@Data
public class ArtappmSelectionBatchUpdateRequest {

    @Valid
    private List<ArtappmSelectionUpdateRequest> list;
}
