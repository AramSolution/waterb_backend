package arami.shared.armuser.dto.response;

import lombok.Data;

@Data
public class ArmuserUserIdCheckResponse {
    private int exist;        // 1: 존재, 0: 미존재
    private String result;    // 00: 성공, 01: 실패
    private String message;   // 메시지
}
