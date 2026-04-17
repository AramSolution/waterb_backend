package arami.shared.armchil.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 자녀 연동 등록 요청 (학생명, 성별, 연락처, 주민등록번호로 자녀 일치 조회)
 */
@Data
public class ArmchilLinkRequest {

    @NotBlank(message = "학생명이 필요합니다.")
    private String userNm;
    @NotBlank(message = "성별이 필요합니다.")
    private String sexdstnCode;
    @NotBlank(message = "연락처가 필요합니다.")
    private String mbtlnum;
    @NotBlank(message = "주민등록번호가 필요합니다.")
    private String ihidnum;
}
