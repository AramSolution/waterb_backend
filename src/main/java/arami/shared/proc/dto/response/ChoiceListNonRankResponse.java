package arami.shared.proc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * f_choicelist 프로시저 결과 (선정된 신청자 1건)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceListNonRankResponse {

    /** 순번 */
    private Integer seqNo;
    /** 신청자 고유 ID */
    private Integer choiSeq;
}
