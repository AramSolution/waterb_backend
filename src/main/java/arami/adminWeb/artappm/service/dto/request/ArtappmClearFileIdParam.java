package arami.adminWeb.artappm.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 지원사업 신청 FILE_ID 초기화용 파라미터 (REQ_ID 기준)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtappmClearFileIdParam {

    private String reqId;
}
