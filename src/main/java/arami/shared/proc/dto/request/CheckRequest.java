package arami.shared.proc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * f_check 함수 호출 파라미터 (자격 조건 확인)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckRequest {

    /** 구분 (01: artappm 등) */
    private String aGubun;
    /** 지원사업 구분 (ARTPROM.PRO_GB 등) */
    private String aProGb;
    /** 사업 ID */
    private String aProId;
    /** 사용자(학생) 고유 ID */
    private String aEsntlId;
}
