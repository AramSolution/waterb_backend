package arami.adminWeb.artedum.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 가맹학원 첨부파일(EDU_FILE) 1건 삭제 요청
 * fileId + seq로 ARTFILE 1건 삭제 후, 해당 fileId에 남은 파일이 없으면 ARTEDUM.EDU_FILE 비움.
 */
@Data
public class ArtedumFileDeleteRequest {

    @NotBlank(message = "학원 ID(eduEsntlId)는 필수입니다.")
    private String eduEsntlId;

    @NotBlank(message = "희망사업(eduGb)은 필수입니다.")
    private String eduGb;

    @NotNull(message = "파일 ID(fileId)는 필수입니다.")
    private Long fileId;

    @NotNull(message = "파일 순번(seq)은 필수입니다.")
    private Integer seq;
}
