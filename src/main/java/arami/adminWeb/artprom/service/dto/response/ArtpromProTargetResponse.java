package arami.adminWeb.artprom.service.dto.response;

import lombok.Data;

/**
 * 지원사업 사업대상(PRO_TARGET) 조회 응답 DTO
 */
@Data
public class ArtpromProTargetResponse {
    private String proId;
    private String proTarget;
}

