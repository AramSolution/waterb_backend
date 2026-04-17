package arami.adminWeb.artappm.service.dto.response;

import lombok.Data;

/** 수강확인증 상세 조회 API 응답 (detail + result, message) */
@Data
public class StudyCertDetailApiResponse {
    private StudyCertDetailResponse detail;
    private String result;
    private String message;
}
