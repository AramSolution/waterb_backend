package arami.adminWeb.support.service.dto.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerPaymentDetailSaveRequest {

    @NotNull
    private Integer seq;

    /** ARTITED.PAY_STA 갱신값(선택). 미납('01') 분만 반영; 완납 분은 요청을 무시하고 로그만 남김 */
    private String paySta;

    @Valid
    private List<SupportFeePayerPaymentRequest> payments = new ArrayList<>();
}
