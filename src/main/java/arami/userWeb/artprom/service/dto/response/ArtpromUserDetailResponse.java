package arami.userWeb.artprom.service.dto.response;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 지원사업 상세 조회 응답 DTO (사용자웹)
 */
@Data
public class ArtpromUserDetailResponse {

    private ArtpromUserDTO detail;
    private List<Map<String, Object>> proFileList;
    private List<Map<String, Object>> fileList;
    private String result;
}
