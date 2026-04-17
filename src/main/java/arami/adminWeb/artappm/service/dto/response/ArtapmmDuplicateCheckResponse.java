package arami.adminWeb.artappm.service.dto.response;

import lombok.Data;

/**
 * 멘토 신청 중복 여부 (동일 PRO_ID·PRO_SEQ·REQ_ESNTL_ID 활성 건 존재).
 */
@Data
public class ArtapmmDuplicateCheckResponse {

    private String result;
    /** true면 이미 신청한 멘토(논리삭제 제외 활성 건 있음) */
    private boolean duplicate;
}
