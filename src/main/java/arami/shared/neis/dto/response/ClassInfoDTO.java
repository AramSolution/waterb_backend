package arami.shared.neis.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * NEIS classInfo API row DTO (학년·반 정보).
 * 역직렬화(NEIS API): @JsonAlias로 대문자 키 매핑.
 * 직렬화(API 응답): camelCase(schulNm, grade, classNm 등)로 출력.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassInfoDTO {

    @JsonAlias("ATPT_OFCDC_SC_CODE")
    private String atptOfcdcScCode;

    @JsonAlias("ATPT_OFCDC_SC_NM")
    private String atptOfcdcScNm;

    @JsonAlias("SD_SCHUL_CODE")
    private String sdSchulCode;

    @JsonAlias("SCHUL_NM")
    private String schulNm;

    @JsonAlias("AY")
    private String ay;

    /** 학년 */
    @JsonAlias("GRADE")
    private String grade;

    @JsonAlias("DGHT_CRSE_SC_NM")
    private String dghtCrseScNm;

    @JsonAlias("SCHUL_CRSE_SC_NM")
    private String schulCrseScNm;

    @JsonAlias("ORD_SC_NM")
    private String ordScNm;

    @JsonAlias("DDDEP_NM")
    private String dddepNm;

    /** 반 */
    @JsonAlias("CLASS_NM")
    private String classNm;

    @JsonAlias("LOAD_DTM")
    private String loadDtm;
}
