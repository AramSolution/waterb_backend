package arami.adminWeb.support.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerDeleteResponse {

    private String result;
    private String message;
    private String itemId;
    private Integer seq;
    /** 산정 행만 삭제한 경우에만 세팅 */
    private Integer seq2;
}
