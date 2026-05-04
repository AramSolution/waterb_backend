package arami.adminWeb.support.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerPaymentDeleteRequest {

    @NotBlank
    private String itemId;

    @NotNull
    private Integer seq;

    /** ARTITEP 납부 행 SEQ2 (필수). */
    @NotNull
    private Integer seq2;
}
