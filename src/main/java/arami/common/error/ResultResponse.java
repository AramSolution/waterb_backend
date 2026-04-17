package arami.common.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "API 응답 결과")
public class ResultResponse<T> {
    @Schema(description = "결과 코드", example = "SUCCESS")
    private final String resultCode;

    @Schema(description = "결과 메시지", example = "성공적으로 처리되었습니다.")
    private final String resultMessage;

    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int status;

    @Schema(description = "응답 데이터")
    private final T data;

    public static <T> ResultResponse<T> success(String message, T data) {
        return ResultResponse.<T>builder()
                .resultCode("SUCCESS")
                .resultMessage(message)
                .status(200)
                .data(data)
                .build();
    }

    public static <T> ResultResponse<T> of(ErrorCode errorCode) {
        return ResultResponse.<T>builder()
                .resultCode(errorCode.getResultCode())
                .resultMessage(errorCode.getResultMessage())
                .status(errorCode.getStatus().value())
                .data(null)
                .build();
    }
}
