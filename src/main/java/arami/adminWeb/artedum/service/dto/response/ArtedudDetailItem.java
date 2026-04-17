package arami.adminWeb.artedum.service.dto.response;

import lombok.Data;

/**
 * 가맹학원 신청 과목 1건 상세 (ARTEDUD 조회용)
 */
@Data
public class ArtedudDetailItem {

    private String eduEsntlId;
    private String eduGb;
    private Integer seq;
    private String subNm;
    private Integer subPay;
    private Integer subCnt;
}
