package arami.shared.armuser.dto.response;

import lombok.Data;

import java.util.List;

/**
 * ARMUSER 엑셀 목록 조회 응답 DTO
 */
@Data
public class ArmuserExcelListResponse {

    /** 엑셀용 사용자 목록 */
    private List<ArmuserDTO> data;
    /** 결과 코드 ("00": 성공, "01": 실패) */
    private String result;
}
