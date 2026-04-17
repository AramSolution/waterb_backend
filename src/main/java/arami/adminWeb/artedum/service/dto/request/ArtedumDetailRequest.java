package arami.adminWeb.artedum.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가맹학원 상세 조회 요청 (eduEsntlId, eduGb)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtedumDetailRequest {

    private String eduEsntlId;
    private String eduGb;
}
