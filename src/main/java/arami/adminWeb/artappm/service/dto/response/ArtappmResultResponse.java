package arami.adminWeb.artappm.service.dto.response;

import lombok.Data;

@Data
public class ArtappmResultResponse {
    private String result;
    private String message;
    /** 멘토 신청(ARTAPMM) 등록 성공 시 부여된 REQ_ID (선택) */
    private String reqId;
}
