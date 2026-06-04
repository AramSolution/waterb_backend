package arami.adminWeb.support.service.dto.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipPaymentSaveRequest {

    @NotBlank
    private String itemId;

    @Valid
    @NotEmpty
    private List<SupportDrainageEquipPaymentDetailSaveRequest> details = new ArrayList<>();
}
