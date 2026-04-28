package arami.adminWeb.support.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerPaymentRequest {

    /**
     * I: 신규, U: 수정, D: 삭제.
     * 미지정 시 seq2가 있으면 U, 없으면 I로 간주.
     */
    private String rowStatus;

    /** 수정/삭제 식별자. 신규(I)는 미지정 권장. */
    private Integer seq2;

    private String payDay;
    private Integer pay;
    private String payDesc;
}
