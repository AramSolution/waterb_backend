package arami.adminWeb.artappm.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 수강확인증 컬럼 초기화용 파라미터 (ARTAPPM.STUDY_CERT = NULL, REQ_ID 기준)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtappmClearStudyCertParam {

    private String reqId;
}
