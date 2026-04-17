package arami.shared.armuser.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import arami.common.files.service.FileDTO;

@Data
public class ArmuserDetailResponse {

    private ArmuserDTO detail;
    /** 회원 사진(USER_PIC)으로 ARTFILE 조회한 목록 (이미지 표시 시 fileId+seq로 /api/v1/files/view 사용) */
    private List<FileDTO> userPicFiles = new ArrayList<>();
    /** 첨부파일(ATTA_FILE) fileId로 조회한 목록 */
    private List<FileDTO> attaFiles = new ArrayList<>();
    /** 사업자등록증(BIZNO_FILE) fileId로 조회한 목록 (1건) */
    private List<FileDTO> biznoFiles = new ArrayList<>();
    private String result;
}
