package arami.adminWeb.artappm.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 변경이력 1건 (f_changlist 프로시저 결과).
 * CHG_DT, CHG_USER_NM, CHG_DESC.
 */
@Data
public class ChangeListItemResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date chgDt;
    private String chgUserNm;
    private String chgDesc;
}
