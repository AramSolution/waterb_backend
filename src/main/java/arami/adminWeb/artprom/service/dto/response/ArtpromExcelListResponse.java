package arami.adminWeb.artprom.service.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 지원사업 엑셀 목록 조회 응답 DTO
 */
@Data
public class ArtpromExcelListResponse {

    /** 엑셀용 지원사업 목록 */
    private List<ArtpromDTO> data;
    /** 결과 코드 ("00": 성공, "01": 실패) */
    private String result;
}
