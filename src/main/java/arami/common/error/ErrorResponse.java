package arami.common.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;
    private final int status;

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getResultCode())
                .message(errorCode.getResultMessage())
                .status(errorCode.getStatus().value())
                .build();
    }
}
