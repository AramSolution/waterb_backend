package arami.adminWeb.artappm.service.dto.request;

import lombok.Data;

/**
 * 수강확인증 업로드 시 multipart "data" 파트용.
 * 화면에서 FILE_DESC(파일설명), UPLOAD_DTTM(업로드일자, 선택), seq(선택)을 보내면 ARTFILE에 저장.
 * seq 없음 → 추가(append). seq 있음 → 해당 (FILE_ID, SEQ) 수정(파일/설명/일자).
 */
@Data
public class StudyCertUploadRequest {

    /** 파일 설명 (ARTFILE.FILE_DESC). 화면에서 전달 */
    private String fileDesc;

    /** 업로드일자 (ARTFILE.UPLOAD_DTTM). 없으면 서버 NOW(). 형식: yyyy-MM-dd 또는 yyyy-MM-dd HH:mm:ss */
    private String uploadDttm;

    /** 수정 시 지정. 없으면 추가(append), 있으면 해당 SEQ 행만 수정(파일·FILE_DESC·UPLOAD_DTTM) */
    private Integer seq;
}
