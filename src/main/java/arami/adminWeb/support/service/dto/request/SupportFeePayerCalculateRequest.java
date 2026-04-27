package arami.adminWeb.support.service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportFeePayerCalculateRequest {

    @NotBlank
    private String itemId;

    @NotNull
    @Min(1)
    private Integer seq;
}
