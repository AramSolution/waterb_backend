package arami.adminWeb.artedum.service.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 가맹학원(희망사업 신청) 엑셀 목록 조회 응답 DTO
 */
@Data
public class ArtedumExcelListResponse {

    private List<ArtedumDTO> data;
    private String result;
}
