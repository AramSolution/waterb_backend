package arami.adminWeb.artprom.service.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 지원사업 일정 목록 API 응답 (ARTPROD 목록)
 */
@Data
public class ArtprodScheduleListResponse {

    private String result;
    private List<ArtprodScheduleItemResponse> data;
}
