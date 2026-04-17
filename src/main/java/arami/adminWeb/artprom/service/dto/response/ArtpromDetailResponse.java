package arami.adminWeb.artprom.service.dto.response;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 지원사업 상세 조회 응답 DTO
 * detail + 홍보파일(proFileList) + 첨부파일(fileList)
 */
@Data
public class ArtpromDetailResponse {

    private ArtpromDTO detail;
    /** 홍보파일 목록 (PRO_FILE_ID로 조회) */
    private List<Map<String, Object>> proFileList;
    /** 첨부파일 목록 (FILE_ID로 조회) */
    private List<Map<String, Object>> fileList;
    private String result;
}
