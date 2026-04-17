package arami.adminWeb.artappm.service.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import arami.common.files.service.FileDTO;

@Data
public class ArtappmDetailResponse {
    private ArtappmDTO detail;
    /** 첨부파일 목록 (fileId 기준 조회) */
    private List<FileDTO> files = new ArrayList<>();
    private String result;
}
