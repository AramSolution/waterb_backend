package arami.shared.proc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * f_selectlist02 프로시저 결과 1건 (space_data, pro_seq)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectList02ItemResponse {

    /** 장소+시간 표시 문자열 (예: 금강도서관 : 10:00~12:00) */
    private String spaceData;
    /** 일정 순번 (PRO_SEQ) */
    private Integer proSeq;
}
