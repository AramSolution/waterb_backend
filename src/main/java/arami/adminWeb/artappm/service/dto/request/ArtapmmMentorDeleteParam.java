package arami.adminWeb.artappm.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ARTAPMM 멘토 한 건 논리 삭제 파라미터 (REQ_ID → STTUS_CODE='D').
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtapmmMentorDeleteParam {
    private String reqId;
}
