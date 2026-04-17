package arami.adminWeb.artadvi.service.dto.request;

import lombok.Data;

/**
 * 상담관리(ARTADVI) 목록 조회 요청 - REQ_ID 기준 (PK 단일)
 */
@Data
public class ArtadviListRequest {
    private String reqId;
}
