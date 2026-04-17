package arami.adminWeb.artappm.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 수강확인증 파일 ID만 UPDATE 시 사용 (ARTAPPM.STUDY_CERT, REQ_ID 기준)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtappmStudyCertUpdateParam {

    private String reqId;
    private String studyCert;
}
