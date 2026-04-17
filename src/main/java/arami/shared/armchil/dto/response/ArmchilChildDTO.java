package arami.shared.armchil.dto.response;

import arami.common.files.service.FileDTO;
import lombok.Data;

import java.util.List;

/**
 * ARMCHIL 자녀 목록 DTO (학부모의 자녀)
 */
@Data
public class ArmchilChildDTO {

    /** 자녀 고유ID (C_ESNTL_ID) */
    private String esntlId;
    /** 자녀 이름 */
    private String userNm;
    /** 연락처 */
    private String mbtlnum;
    /** 성별코드 */
    private String sexdstnCode;
    /** 성별명 (M=남자, F=여자) */
    private String sexdstnCodeNm;
    /** 목록 순번 (RNUM) */
    private Integer rnum;
    /** 생년월일 (yyyy-MM-dd) */
    private String brthdy;
    /** 주민등록번호 */
    private String ihidnum;
    /** 프로필 사진 FILE_ID (USER_PIC) */
    private String userPic;
    /** 프로필 사진 파일 목록 (이미지 표시 시 fileId+seq로 /api/v1/files/view 사용) */
    private List<FileDTO> userPicFiles;
}
