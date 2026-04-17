package arami.adminWeb.artedum.service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 가맹학원 신청 과목 1건 (ARTEDUD)
 * 등록 시 seq 없음. 수정 시 기존 과목은 seq 있음, 추가 과목은 seq 없음.
 */
@Data
public class ArtedumSubjectItem {

    /** 순번 (수정 시 기존 과목에만 있음, 없거나 0이면 INSERT) */
    private Integer seq;
    /** 취급과목 */
    @Size(max = 128)
    private String subNm;
    /** 수강료 */
    private Integer subPay;
    /** 모집가능인원 */
    private Integer subCnt;
}
