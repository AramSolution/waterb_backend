package arami.adminWeb.artchoi.service.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ArtchoiSelectionBatchInsertRequest {
    @Valid
    private List<ArtchoiResultGbUpdateParam> list;
    /** 선정인원수 */
    private Integer selectCnt;
    /** 예비인원수 */
    private Integer reserveCnt;
}
