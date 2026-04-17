package arami.adminWeb.artappm.service.dto.response;

import java.util.List;
import lombok.Data;

/**
 * 지원사업 신청 엑셀 목록 조회 응답 DTO
 */
@Data
public class ArtappmExcelListResponse {

    /** 엑셀용 지원사업 신청 목록 */
    private List<ArtappmDTO> data;
    /** 결과 코드 ("00": 성공, "01": 실패) */
    private String result;
}
