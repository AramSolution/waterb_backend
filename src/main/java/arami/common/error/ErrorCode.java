package arami.common.error;

import egovframework.com.cmm.service.EgovProperties;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    UNAUTHORIZED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACCESS_TOKEN", "인가된 사용자가 아닙니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "허용되지 않은 메소드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부에 오류가 발생했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),

    // OAuth Link (MY PAGE)
    OAUTH_ALREADY_LINKED(HttpStatus.CONFLICT, "OAUTH_ALREADY_LINKED", "이미 연동된 아이디입니다."),

    // Encryption
    ENCRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ENCRYPTION_FAILED", "데이터 암호화 중 오류가 발생했습니다."),

    // File
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_NOT_FOUND", "업로드 할 파일이 없습니다."),
    FILE_MAX_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE_MAX_SIZE_EXCEEDED", ""),
    FILE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FILE_NOT_ALLOWED", "허용되지 않는 파일 형식 입니다."),
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_FAIL", "파일 업로드에 실패했습니다.");

    private final HttpStatus status;
    private final String resultCode;
    private final String resultMessage;

    ErrorCode(HttpStatus status, String resultCode, String resultMessage) {
        this.status = status;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public String getResultMessage() {
        if (this == FILE_MAX_SIZE_EXCEEDED) {
            return getFileMaxSizeMessage();
        }
        return resultMessage;
    }

    public static String getFileMaxSizeMessage() {
        try {
            long size = NumberUtils.createLong(EgovProperties.getProperty("Globals.posblAtchFileSize"));
            return "파일 크기(" + (size / 1024 / 1024) + "MB) 제한을 초과했습니다.";
        } catch (Exception e) {
            return "파일 크기 제한을 초과했습니다.";
        }
    }
}
