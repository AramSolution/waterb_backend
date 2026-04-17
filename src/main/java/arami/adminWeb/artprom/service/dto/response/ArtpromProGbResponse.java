package arami.adminWeb.artprom.service.dto.response;

import lombok.Data;

/**
 * 지원사업(PRO_ID) 단건 조회 시 PRO_GB(사업구분) 응답 DTO
 */
@Data
public class ArtpromProGbResponse {
    private String proId;
    private String proGb;
}

