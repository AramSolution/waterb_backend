package arami.adminWeb.artappm.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 선정여부(RESULT_GB)만 UPDATE 시 사용 (ARTAPPM, REQ_ID 기준)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtappmResultGbUpdateParam {

    private String reqId;
    private String resultGb;
    private String chgUserId;
}
