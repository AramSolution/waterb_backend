package arami.shared.neis.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * NEIS schoolInfo API row 항목 DTO.
 * 역직렬화(NEIS API): @JsonAlias로 대문자 키 매핑.
 * 직렬화(API 응답): camelCase(schulNm 등)로 출력.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDTO {

    @JsonAlias("ATPT_OFCDC_SC_CODE")
    private String atptOfcdcScCode;

    @JsonAlias("ATPT_OFCDC_SC_NM")
    private String atptOfcdcScNm;

    @JsonAlias("SD_SCHUL_CODE")
    private String sdSchulCode;

    @JsonAlias("SCHUL_NM")
    private String schulNm;

    @JsonAlias("ENG_SCHUL_NM")
    private String engSchulNm;

    @JsonAlias("SCHUL_KND_SC_NM")
    private String schulKndScNm;

    @JsonAlias("LCTN_SC_NM")
    private String lctnScNm;

    @JsonAlias("JU_ORG_NM")
    private String juOrgNm;

    @JsonAlias("FOND_SC_NM")
    private String fondScNm;

    @JsonAlias("ORG_RDNZC")
    private String orgRdnzc;

    @JsonAlias("ORG_RDNMA")
    private String orgRdnma;

    @JsonAlias("ORG_RDNDA")
    private String orgRdnda;

    @JsonAlias("ORG_TELNO")
    private String orgTelno;

    @JsonAlias("HMPG_ADRES")
    private String hmpgAdres;

    @JsonAlias("COEDU_SC_NM")
    private String coeduScNm;

    @JsonAlias("ORG_FAXNO")
    private String orgFaxno;

    @JsonAlias("HS_SC_NM")
    private String hsScNm;

    @JsonAlias("INDST_SPECL_CCCCL_EXST_YN")
    private String indstSpeclCccclExstYn;

    @JsonAlias("HS_GNRL_BUSNS_SC_NM")
    private String hsGnrlBusnsScNm;

    @JsonAlias("SPCLY_PURPS_HS_ORD_NM")
    private String spclyPurpsHsOrdNm;

    @JsonAlias("ENE_BFE_SEHF_SC_NM")
    private String eneBfeSehfScNm;

    @JsonAlias("DGHT_SC_NM")
    private String dghtScNm;

    @JsonAlias("FOND_YMD")
    private String fondYmd;

    @JsonAlias("FOAS_MEMRD")
    private String foasMemrd;

    @JsonAlias("LOAD_DTM")
    private String loadDtm;
}
