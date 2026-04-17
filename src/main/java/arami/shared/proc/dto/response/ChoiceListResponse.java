package arami.shared.proc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * f_choicelist 프로시저 결과 (선정된 신청자 1건)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceListResponse {

    /** 순번 */
    private Integer seqNo;
    /** 신청자 고유 ID (학생키로만 내려오게 변경) */
    @JsonProperty("cEsntlId")
    private String cEsntlId;
}
