package arami.shared.proc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * f_selectlist02 프로시저 요청 (상담일자별 상담장소/시간 목록)
 * CALL f_selectlist02(aPRO_ID, aWORK_DT)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectList02Request {

    /** 사업 ID (PRO_ID) - 프로시저 in aPRO_ID */
    private String aProId;
    /** 상담일자 (YYYY-MM-DD) - 프로시저 in aWORK_DT */
    private String aWorkDt;

    /** MyBatis 매핑 호환: XML에서 #{proId} 사용 시 */
    public String getProId() {
        return aProId;
    }

    /** MyBatis 매핑 호환: XML에서 #{consultDate} 사용 시 */
    public String getConsultDate() {
        return aWorkDt;
    }
}
