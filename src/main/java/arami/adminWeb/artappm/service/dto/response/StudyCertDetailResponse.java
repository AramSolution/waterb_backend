package arami.adminWeb.artappm.service.dto.response;

import lombok.Data;
import java.util.Date;

/**
 * 수강확인증 상세 1건.
 * 화면: 일자(UPLOAD_DTTM), 내용(FILE_DESC), 첨부파일 1건(fileId, seq, orgfNm).
 */
@Data
public class StudyCertDetailResponse {

    /** 업로드일시 (ARTFILE.UPLOAD_DTTM) - 일자 */
    private Date uploadDttm;
    /** 파일 설명 (ARTFILE.FILE_DESC) - 내용 */
    private String fileDesc;
    /** 파일 ID (다운로드/재저장 시 사용, 문자열 유지) */
    private String fileId;
    /** 파일 순번 */
    private Integer seq;
    /** 원본 파일명 (다운로드 표시용) */
    private String orgfNm;
}
