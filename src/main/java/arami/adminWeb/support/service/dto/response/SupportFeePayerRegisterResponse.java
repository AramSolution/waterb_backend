package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerRegisterResponse {

    private String result;
    private String message;
    private String itemId;

    /** 완납 등으로 수정·삭제가 적용되지 않은 detail 목록 (없으면 빈 배열 권장) */
    private List<SupportFeePayerRegisterSkippedDetailResponse> skippedDetails;
}
