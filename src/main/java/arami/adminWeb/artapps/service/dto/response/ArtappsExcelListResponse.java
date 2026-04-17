package arami.adminWeb.artapps.service.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 공부의명수 엑셀 목록 조회 응답 DTO
 */
@Data
public class ArtappsExcelListResponse {

    /** 엑셀용 공부의명수 목록 */
    private List<ArtappsDTO> data;  
    /** 결과 코드 ("00": 성공, "01": 실패) */
    private String result;
}
