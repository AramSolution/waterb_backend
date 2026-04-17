package arami.shared.proc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * f_check 함수 결과 (자격 조건 확인)
 * Y: 통과, N|...: 미통과(이유 코드), E: 예외
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckResponse {

    /** 결과 (Y / N|코드 / E) */
    private String result;
}
