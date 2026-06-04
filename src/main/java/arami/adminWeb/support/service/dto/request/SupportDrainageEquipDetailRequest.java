package arami.adminWeb.support.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportDrainageEquipDetailRequest {

    /**
     * 행 처리 구분.
     * - I: 신규 등록 (seq 미지정 권장, 서버 채번)
     * - U: 수정 (기존 seq 필수)
     * - D: 삭제 (기존 seq 필수)
     * 미지정 또는 공백이면 해당 행은 변경 없음으로 처리한다.
     */
    private String rowStatus;

    /** 기존 ARTEQUD 행 대상으로 수정/삭제 시 해당 SEQ를 지정. */
    private Integer seq;

    private String paySta;

    /** 등록일 */
    private String reqDate;

    /** 착공일 */
    private String startDate;

    /** 준공예정일 */
    private String planDate;

    /** 준공일 */
    private String compDate;

    /** 대행업체 */
    private String agency;

    /** 배수설비금액 (직접 입력, 계산 없음) */
    private Integer equipCost;

    /** 납부금액 */
    private Integer equipPay;
}
