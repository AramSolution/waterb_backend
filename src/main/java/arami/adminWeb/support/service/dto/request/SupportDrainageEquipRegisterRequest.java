package arami.adminWeb.support.service.dto.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipRegisterRequest {

    /**
     * 생략 시 신규 ITEM_ID 채번 후 ARTEQUM 등록.
     * 지정 시 해당 ARTEQUM이 존재해야 하며 기본정보를 갱신한 뒤 details 배열 단위로 ARTEQUD UPSERT.
     */
    private String itemId;

    @Valid
    @NotNull
    private SupportDrainageEquipBasicInfoRequest basicInfo;

    @Valid
    @NotNull
    @NotEmpty
    private List<SupportDrainageEquipDetailRequest> details = new ArrayList<>();
}
