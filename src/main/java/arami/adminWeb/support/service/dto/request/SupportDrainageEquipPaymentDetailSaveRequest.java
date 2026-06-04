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
public class SupportDrainageEquipPaymentDetailSaveRequest {

    @NotNull
    private Integer seq;

    private String paySta;

    @Valid
    private List<SupportDrainageEquipPaymentRequest> payments = new ArrayList<>();
}
