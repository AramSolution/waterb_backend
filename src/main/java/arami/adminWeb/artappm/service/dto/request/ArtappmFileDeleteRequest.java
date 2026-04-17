package arami.adminWeb.artappm.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 지원사업 신청 첨부파일 1건 삭제 요청 DTO (REQ_ID 기준).
 * fileId + seq로 ARTFILE 1건 삭제 후, 해당 fileId에 남은 파일이 없으면 ARTAPPM.FILE_ID를 비움.
 */
@Data
public class ArtappmFileDeleteRequest {

    @NotBlank(message = "지원사업신청ID(reqId)는 필수입니다.")
    private String reqId;

    @NotNull(message = "파일 ID(fileId)는 필수입니다.")
    private Long fileId;

    @NotNull(message = "파일 순번(seq)은 필수입니다.")
    private Integer seq;
}
