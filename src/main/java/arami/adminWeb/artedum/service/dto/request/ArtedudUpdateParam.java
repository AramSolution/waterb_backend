package arami.adminWeb.artedum.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ARTEDUD 과목 1건 수정용 파라미터 (Mapper)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtedudUpdateParam {

    private String eduEsntlId;
    private String eduGb;
    private int seq;
    private String subNm;
    private Integer subPay;
    private Integer subCnt;
}
