package arami.shared.proc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * f_selectlist01 프로시저 요청 (사업ID + 기준년월별 목록)
 * CALL f_selectlist01(aPRO_ID, aWORK_YM)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectList01Request {

    /** 사업 ID (PRO_ID) - 프로시저 in aPRO_ID */
    private String aProId;
    /** 기준년월 (YYYYMM) - 프로시저 in aWORK_YM */
    private String aWorkYm;
}
