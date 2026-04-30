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
     * 미지정 또는 공백이면 해당 행은 변경 없음으로 처리되어 서버에서 납부내역(ARTITEP) 갱신을 하지 않는다.
     */
    private String rowStatus;

    /** 수정/삭제 식별자. 신규(I)는 미지정 권장. */
    private Integer seq2;

    private String payDay;
    private Integer pay;
    private String payDesc;
}
