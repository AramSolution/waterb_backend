package arami.adminWeb.artappm.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 수강확인증 목록 1건 (ARTAPPM + ARTFILE 조인).
 * 지원사업당 STUDY_CERT = FILE_ID로 ARTFILE에 여러 건(SEQ) 가능하므로, 한 행 = 한 개의 수강확인증 파일.
 */
@Data
public class StudyCertListItemResponse {

    private String rnum;
    /** 지원사업신청ID (REQ_ID, 상세/삭제 by-req-id용) */
    private String reqId;
    private String proId;
    private String proSeq;
    private String reqEsntlId;
    /** ARTFILE FILE_ID (수강확인증 그룹) */
    private Long fileId;
    /** ARTFILE SEQ (동일 FILE_ID 내 순번) */
    private Integer seq;
    /** ARTFILE.UPLOAD_DTTM (API 응답: Asia/Seoul 기준 날짜만 yyyy-MM-dd) */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date uploadDttm;
    /** ARTFILE.FILE_DESC */
    private String fileDesc;
}
