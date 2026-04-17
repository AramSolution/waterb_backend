package arami.shared.proc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * f_choicelist 프로시저 호출 파라미터 (랜덤 신청자 선택)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceListRequest {

    /** 구분 (01: artappm 등) */
    @JsonProperty("aGubun")
    private String aGubun;
    /** 사업 ID */
    @JsonProperty("aProId")
    private String aProId;
    /** 사업 회차 */
    @JsonProperty("aProSeq")
    private Integer aProSeq;
    /** 선정 인원 수 */
    @JsonProperty("aDataCnt")
    private Integer aDataCnt;
    /** 선정 순위/옵션 (파이프 구분 문자열, 예: 01|00|00|00) */
    @JsonProperty("aRank")
    private String aRank;
}
