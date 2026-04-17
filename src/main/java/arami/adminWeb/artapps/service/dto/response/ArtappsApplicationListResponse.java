package arami.adminWeb.artapps.service.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 공부의 명수 신청목록 조회 응답 ({@link arami.adminWeb.artprom.service.dto.response.ArtpromListResponse}와 동일 구조).
 */
@Data
public class ArtappsApplicationListResponse {

    private List<ArtappsApplicationListRowDTO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private String result;
}
