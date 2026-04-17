package arami.shared.proc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * f_selectlist01 프로시저 결과 1건 (공공형 진로진학 컨설팅_상담일정 달력조회).
 * 결과 컬럼: GBN_DATA, SPACE_NM, RUNTIME, USE_CNT, USE_YN → camelCase 자동 매핑.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectList01ItemResponse {

    /** 일(日) 값 (DATE_FORMAT %d, 해당 월 내 일자) */
    private String gbnData;
    /** 장소명 (EDR004 코드명) */
    private String spaceNm;
    /** 운영시간 (HH:mm~HH:mm) */
    private String runtime;
    /** 사용(신청) 건수 */
    private Integer useCnt;
    /** 가용 여부 (T: 가용, F: 마감) */
    private String useYn;
}
