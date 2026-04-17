package arami.common.files.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 파일 메타 정보 DTO.
 * ARTFILE 테이블 및 업로드/조회 API에서 사용.
 */
@Data
@Schema(description = "파일 업로드 DTO")
public class FileDTO {

    /** 파일 그룹 ID (동일 그룹 내 파일들이 같은 fileId 공유). JS 정밀도 한계 회피를 위해 문자열로 직렬화 */
    @Schema(description = "파일ID", example = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fileId;

    /** 파일 순번 (fileId 내에서 1부터 증가) */
    @Schema(description = "번호", example = "파일번호")
    private int seq;

    /** 저장구분: L=로컬, O=원격(S3) */
    @Schema(description = "저장구분", example = "L:로컬저장, O:원격(S3)저장")
    private String saveGb;

    /** 디스크 저장 경로 */
    @Schema(description = "저장경로", example = "파일 저장 경로")
    private String filePath;

    /** 원본 파일명 */
    @Schema(description = "원본파일명", example = "원본 파일명")
    private String orgfNm;

    /** 저장 파일명 (중복 방지용 생성명) */
    @Schema(description = "저장파일명", example = "저장 파일명")
    private String saveNm;

    /** 파일 확장자 */
    @Schema(description = "확장자명", example = "파일 확장자명")
    private String fileExt;

    /** MIME 타입 */
    @Schema(description = "파일유형", example = "파일 유형")
    private String fileType;

    /** 파일 크기(byte) */
    @Schema(description = "용량", example = "파일 용량")
    private float fileSize;

    /** multipart 필드명 등 태그용 */
    @Schema(description = "파일태그명", example = "파일 태그명")
    private String tagNm;

    /** 파일 설명 */
    @Schema(description = "파일설명", example = "파일 설명")
    private String fileDesc;

    /** 업로드일시 (ARTFILE.UPLOAD_DTTM). null이면 INSERT 시 NOW() 사용 */
    @Schema(description = "업로드일시", example = "2025-02-25 14:30:00")
    private java.util.Date uploadDttm;

    /** 상태코드 (예: A=정상) */
    @Schema(description = "상태코드", example = "파일 상태 코드")
    private String sttusCode;

    /** 수정자 ID (로그인 사용자 uniqId, ARTFILE.CHG_USER_ID). SQL #{UNIQ_ID}와 매핑 */
    @Schema(description = "수정자ID(UNIQ_ID)", example = "uniqId")
    private String UNIQ_ID;

}
