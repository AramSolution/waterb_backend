package arami.adminWeb.support.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipUnpaidListRequest {

    @NotBlank
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$")
    private String baseMonth;

    private Integer startIndex;
    private Integer lengthPage;
    private Integer start;
    private Integer length;

    public void setDefaultPaging() {
        if (startIndex == null && start != null) {
            startIndex = start;
        }
        if (lengthPage == null && length != null) {
            lengthPage = length;
        }
        if (startIndex == null || startIndex < 0) {
            startIndex = 0;
        }
        if (lengthPage == null || lengthPage <= 0) {
            lengthPage = 15;
        }
    }
}
