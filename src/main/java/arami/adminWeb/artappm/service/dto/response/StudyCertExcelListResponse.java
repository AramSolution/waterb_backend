package arami.adminWeb.artappm.service.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 수강확인증 엑셀 목록 조회 응답 DTO
 */
@Data
public class StudyCertExcelListResponse {

    /** 엑셀용 수강확인증 목록 */
    private List<StudyCertListItemResponse> data;
    /** 결과 코드 ("00": 성공, "01": 실패) */
    private String result;

    public void setData(List<StudyCertListItemResponse> data) { this.data = data; }
    public void setResult(String result) { this.result = result; }
}
