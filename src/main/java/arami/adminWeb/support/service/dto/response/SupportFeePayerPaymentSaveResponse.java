package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerPaymentSaveResponse {

    private String result;
    private String message;
    private String itemId;

    /**
     * 적용되지 않은 처리: 완납(PAID), 납부 수정(U 요청 시 UPDATE_NOT_ALLOWED 등).
     * {@link SupportFeePayerRegisterSkippedDetailResponse} 필드 의미 동일.
     */
    private List<SupportFeePayerRegisterSkippedDetailResponse> skippedDetails;
}
