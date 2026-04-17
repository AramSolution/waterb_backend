package arami.shared.proc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * f_changlist 프로시저 호출 파라미터 (변경 이력 조회)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChanglistRequest {

    /** 구분 (01: artappm 등) */
    private String aGubun;
    /** 사업 ID */
    private String aProId;
    /** 사업 회차 */
    private Integer aProSeq;
    /** 신청자 고유 ID */
    private String aReqEsntlId;
}
