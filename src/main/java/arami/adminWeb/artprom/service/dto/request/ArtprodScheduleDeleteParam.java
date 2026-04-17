package arami.adminWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * ARTPROD 일정 한 건 삭제 파라미터 (PRO_ID + PRO_SEQ)
 */
@Data
public class ArtprodScheduleDeleteParam {

    private String proId;
    private Integer proSeq;
}
