package arami.adminWeb.artedum.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ARTEDUD 1건 INSERT용 파라미터 (Mapper)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtedudInsertParam {

    private String eduEsntlId;
    private String eduGb;
    private int seq;
    private String subNm;
    private Integer subPay;
    private Integer subCnt;
    private String sttusCode;
    private String chgUserId;
}
