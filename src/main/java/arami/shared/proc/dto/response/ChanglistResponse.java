package arami.shared.proc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * f_changlist 프로시저 결과 (변경 이력 1건)
 * 프로시저 SELECT의 두 번째 컬럼은 AS change_desc 로 alias 지정 시 매핑됩니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChanglistResponse {

    /** 변경 일시 */
    private String chgDt;
    /** 변경 내용 요약 (concat 결과) */
    private String changeDesc;
}
