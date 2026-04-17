package arami.adminWeb.artprom.service.dto.request;

import java.util.List;

import lombok.Data;

/**
 * 일정 목록 저장 요청 (PUT /api/admin/artprom/{proId}/schedule)
 */
@Data
public class ArtprodScheduleSaveRequest {

    private List<ArtprodScheduleItemSaveRequest> items;
}
